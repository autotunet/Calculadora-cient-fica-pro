package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CalculationItem
import com.example.data.CalculationRepository
import com.example.math.AngleUnit
import com.example.math.FractionHelper
import com.example.math.MathEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class KeypadCategory {
    SCIENTIFIC,
    BASIC,
    CONSTANTS_MEMORY
}

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "0",
    val previewResult: String? = null,
    val fractionResult: String? = null,
    val angleUnit: AngleUnit = AngleUnit.DEG,
    val isSecondFunction: Boolean = false,
    val isHyperbolic: Boolean = false,
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,
    val lastAnswer: String = "0",
    val activeCategory: KeypadCategory = KeypadCategory.SCIENTIFIC,
    val history: List<CalculationItem> = emptyList(),
    val errorMessage: String? = null,
    val statusToast: String? = null,
    val isCalculated: Boolean = false
)

class CalculatorViewModel(
    private val repository: CalculationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.historyFlow.collect { list ->
                _uiState.update { it.copy(history = list) }
            }
        }
    }

    fun onAppendText(token: String, isFunction: Boolean = false) {
        val currentState = _uiState.value

        // If a result was just calculated and the user taps an operator,
        // continue operating on the result:
        val startsWithOperator = token in listOf("+", "−", "×", "÷", "^", "%", "mod")
        val baseExpr = if (currentState.isCalculated) {
            if (startsWithOperator) {
                currentState.result
            } else {
                ""
            }
        } else {
            currentState.expression
        }

        val toAppend = if (isFunction) "$token(" else token
        val newExpr = baseExpr + toAppend

        _uiState.update {
            it.copy(
                expression = newExpr,
                isCalculated = false,
                errorMessage = null
            )
        }
        updateLivePreview(newExpr, _uiState.value.angleUnit)
    }

    fun onInsertConstant(name: String) {
        onAppendText(name, isFunction = false)
    }

    fun onInsertLastAnswer() {
        val ans = _uiState.value.lastAnswer
        if (ans.isNotBlank()) {
            onAppendText(ans, isFunction = false)
        }
    }

    fun onDeleteBack() {
        val current = _uiState.value.expression
        if (current.isEmpty()) return

        // Smart delete for multi-character function tags like "sin(", "asin(", "cosh(", "sqrt(", "log("
        val funcTokens = listOf(
            "sin⁻¹(", "cos⁻¹(", "tan⁻¹(", "cot⁻¹(", "sec⁻¹(", "csc⁻¹(",
            "asinh(", "acosh(", "atanh(",
            "sinh(", "cosh(", "tanh(", "coth(", "sech(", "csch(",
            "asin(", "acos(", "atan(",
            "sin(", "cos(", "tan(", "cot(", "sec(", "csc(",
            "sqrt(", "cbrt(", "nroot(",
            "log10(", "log2(", "logb(", "log(", "ln(",
            "fact(", "nCr(", "nPr(", "abs(", "mod("
        )

        var newExpr = current
        var matched = false
        for (func in funcTokens) {
            if (current.endsWith(func)) {
                newExpr = current.substring(0, current.length - func.length)
                matched = true
                break
            }
        }

        if (!matched) {
            newExpr = current.dropLast(1)
        }

        _uiState.update {
            it.copy(
                expression = newExpr,
                isCalculated = false,
                errorMessage = null
            )
        }
        updateLivePreview(newExpr, _uiState.value.angleUnit)
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                previewResult = null,
                fractionResult = null,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    fun onAllClear() {
        _uiState.update {
            it.copy(
                expression = "",
                result = "0",
                previewResult = null,
                fractionResult = null,
                errorMessage = null,
                isCalculated = false
            )
        }
    }

    fun onCalculate() {
        val expr = _uiState.value.expression.trim()
        if (expr.isEmpty()) return

        val engine = MathEngine(_uiState.value.angleUnit)
        try {
            val eval = engine.evaluate(expr)
            val formatted = MathEngine.formatResult(eval)
            val fraction = FractionHelper.toFractionString(eval)

            viewModelScope.launch {
                repository.saveCalculation(
                    expression = expr,
                    result = formatted,
                    angleUnit = _uiState.value.angleUnit.label
                )
            }

            _uiState.update {
                it.copy(
                    result = formatted,
                    fractionResult = fraction,
                    previewResult = null,
                    lastAnswer = formatted,
                    errorMessage = null,
                    isCalculated = true
                )
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: "Error de cálculo"
            _uiState.update {
                it.copy(
                    errorMessage = errorMsg,
                    previewResult = null,
                    fractionResult = null
                )
            }
        }
    }

    fun onSetExpressionAndResult(newExpr: String, newResult: String) {
        _uiState.update {
            it.copy(
                expression = newExpr,
                result = newResult,
                lastAnswer = newResult,
                previewResult = null,
                isCalculated = true,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            repository.saveCalculation(
                expression = newExpr,
                result = newResult,
                angleUnit = _uiState.value.angleUnit.label
            )
        }
    }

    private fun updateLivePreview(expr: String, angleUnit: AngleUnit) {
        if (expr.isBlank()) {
            _uiState.update { it.copy(previewResult = null, fractionResult = null) }
            return
        }

        val engine = MathEngine(angleUnit)
        val previewVal = engine.preview(expr)
        if (previewVal != null && !previewVal.isNaN() && !previewVal.isInfinite()) {
            val formatted = MathEngine.formatResult(previewVal)
            val fraction = FractionHelper.toFractionString(previewVal)
            _uiState.update {
                it.copy(
                    previewResult = formatted,
                    fractionResult = fraction
                )
            }
        } else {
            _uiState.update { it.copy(previewResult = null, fractionResult = null) }
        }
    }

    fun onToggleAngleUnit() {
        val nextUnit = _uiState.value.angleUnit.next()
        _uiState.update { it.copy(angleUnit = nextUnit) }
        updateLivePreview(_uiState.value.expression, nextUnit)
    }

    fun onToggleSecond() {
        _uiState.update { it.copy(isSecondFunction = !it.isSecondFunction) }
    }

    fun onToggleHyperbolic() {
        _uiState.update { it.copy(isHyperbolic = !it.isHyperbolic) }
    }

    fun setActiveCategory(category: KeypadCategory) {
        _uiState.update { it.copy(activeCategory = category) }
    }

    // Memory Functions (MC, MR, M+, M-, MS)
    fun onMemoryClear() {
        _uiState.update {
            it.copy(
                memoryValue = 0.0,
                hasMemory = false,
                statusToast = "Memoria borrada"
            )
        }
    }

    fun onMemoryRecall() {
        val mem = _uiState.value.memoryValue
        val formatted = MathEngine.formatResult(mem)
        onAppendText(formatted, isFunction = false)
        _uiState.update { it.copy(statusToast = "Memoria recuperada: $formatted") }
    }

    fun onMemoryAdd() {
        val currentVal = evaluateCurrentOrResult() ?: return
        val newMem = _uiState.value.memoryValue + currentVal
        _uiState.update {
            it.copy(
                memoryValue = newMem,
                hasMemory = true,
                statusToast = "Añadido a memoria (M: ${MathEngine.formatResult(newMem)})"
            )
        }
    }

    fun onMemorySub() {
        val currentVal = evaluateCurrentOrResult() ?: return
        val newMem = _uiState.value.memoryValue - currentVal
        _uiState.update {
            it.copy(
                memoryValue = newMem,
                hasMemory = true,
                statusToast = "Restado de memoria (M: ${MathEngine.formatResult(newMem)})"
            )
        }
    }

    fun onMemoryStore() {
        val currentVal = evaluateCurrentOrResult() ?: return
        _uiState.update {
            it.copy(
                memoryValue = currentVal,
                hasMemory = true,
                statusToast = "Guardado en memoria (M: ${MathEngine.formatResult(currentVal)})"
            )
        }
    }

    private fun evaluateCurrentOrResult(): Double? {
        val state = _uiState.value
        return if (state.isCalculated) {
            state.result.replace(" ", "").replace("×10^", "e").toDoubleOrNull()
        } else if (state.expression.isNotBlank()) {
            try {
                MathEngine(state.angleUnit).evaluate(state.expression)
            } catch (_: Exception) {
                null
            }
        } else {
            state.result.toDoubleOrNull()
        }
    }

    fun onUseHistoryItem(item: CalculationItem, useResult: Boolean) {
        val textToUse = if (useResult) item.result else item.expression
        _uiState.update {
            it.copy(
                expression = textToUse,
                isCalculated = false,
                errorMessage = null
            )
        }
        updateLivePreview(textToUse, _uiState.value.angleUnit)
    }

    fun onClearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.update { it.copy(statusToast = "Historial eliminado") }
        }
    }

    fun onDeleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Resultado Calculadora", text)
        clipboard.setPrimaryClip(clip)
        _uiState.update { it.copy(statusToast = "Copiado al portapapeles") }
    }

    fun clearStatusToast() {
        _uiState.update { it.copy(statusToast = null) }
    }
}

class CalculatorViewModelFactory(
    private val repository: CalculationRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
