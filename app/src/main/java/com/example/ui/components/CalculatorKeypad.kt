package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KeypadCategory
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.InterFont
import com.example.ui.theme.KeypadFunction
import com.example.ui.theme.KeypadFunctionText
import com.example.ui.theme.KeypadNumber
import com.example.ui.theme.KeypadNumberText
import com.example.ui.theme.KeypadOperator
import com.example.ui.theme.KeypadOperatorText

@Composable
fun CalculatorKeypad(
    modifier: Modifier = Modifier,
    activeCategory: KeypadCategory,
    isSecondFunction: Boolean,
    isHyperbolic: Boolean,
    onSelectCategory: (KeypadCategory) -> Unit,
    onAppendToken: (String, Boolean) -> Unit,
    onDeleteBack: () -> Unit,
    onClear: () -> Unit,
    onAllClear: () -> Unit,
    onCalculate: () -> Unit,
    onToggleSecond: () -> Unit,
    onToggleHyp: () -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySub: () -> Unit,
    onMemoryStore: () -> Unit,
    onInsertAns: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Category Switcher Tabs
        CategoryTabs(
            activeCategory = activeCategory,
            onSelectCategory = onSelectCategory
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeCategory) {
                KeypadCategory.SCIENTIFIC -> {
                    ScientificKeypadLayout(
                        modifier = Modifier.fillMaxSize(),
                        isSecond = isSecondFunction,
                        isHyp = isHyperbolic,
                        onAppend = onAppendToken,
                        onDeleteBack = onDeleteBack,
                        onClear = onClear,
                        onAllClear = onAllClear,
                        onCalculate = onCalculate,
                        onToggleSecond = onToggleSecond,
                        onToggleHyp = onToggleHyp,
                        onInsertAns = onInsertAns
                    )
                }
                KeypadCategory.BASIC -> {
                    BasicKeypadLayout(
                        modifier = Modifier.fillMaxSize(),
                        onAppend = onAppendToken,
                        onDeleteBack = onDeleteBack,
                        onClear = onClear,
                        onAllClear = onAllClear,
                        onCalculate = onCalculate,
                        onInsertAns = onInsertAns
                    )
                }
                KeypadCategory.CONSTANTS_MEMORY -> {
                    ConstantsAndMemoryLayout(
                        modifier = Modifier.fillMaxSize(),
                        onAppend = onAppendToken,
                        onMemoryClear = onMemoryClear,
                        onMemoryRecall = onMemoryRecall,
                        onMemoryAdd = onMemoryAdd,
                        onMemorySub = onMemorySub,
                        onMemoryStore = onMemoryStore,
                        onClear = onClear,
                        onCalculate = onCalculate,
                        onDeleteBack = onDeleteBack
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    activeCategory: KeypadCategory,
    onSelectCategory: (KeypadCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF131924))
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val categories = listOf(
            KeypadCategory.SCIENTIFIC to "Científica",
            KeypadCategory.BASIC to "Básica",
            KeypadCategory.CONSTANTS_MEMORY to "Memoria & Const"
        )

        categories.forEach { (cat, label) ->
            val isSelected = activeCategory == cat
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color(0xFF222D42) else Color.Transparent)
                    .clickable { onSelectCategory(cat) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontFamily = InterFont,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (isSelected) ElectricCyan else Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun ScientificKeypadLayout(
    modifier: Modifier = Modifier,
    isSecond: Boolean,
    isHyp: Boolean,
    onAppend: (String, Boolean) -> Unit,
    onDeleteBack: () -> Unit,
    onClear: () -> Unit,
    onAllClear: () -> Unit,
    onCalculate: () -> Unit,
    onToggleSecond: () -> Unit,
    onToggleHyp: () -> Unit,
    onInsertAns: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Scientific Row 1 (Modifiers: 2nd, HYP, (, ), Ans)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "2nd",
                backgroundColor = if (isSecond) Color(0xFF382352) else Color(0xFF1E2436),
                contentColor = if (isSecond) Color(0xFFE9D5FF) else Color(0xFFC084FC),
                isActive = isSecond,
                fontSize = 14.sp,
                testTag = "btn_second",
                onClick = onToggleSecond
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "HYP",
                backgroundColor = if (isHyp) Color(0xFF1B3838) else Color(0xFF1A2633),
                contentColor = if (isHyp) Color(0xFF99F6E4) else Color(0xFF5EEAD4),
                isActive = isHyp,
                fontSize = 14.sp,
                testTag = "btn_hyp",
                onClick = onToggleHyp
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "(",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 18.sp,
                testTag = "btn_open_paren",
                onClick = { onAppend("(", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = ")",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 18.sp,
                testTag = "btn_close_paren",
                onClick = { onAppend(")", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "Ans",
                backgroundColor = KeypadFunction,
                contentColor = ElectricCyan,
                fontSize = 14.sp,
                testTag = "btn_ans",
                onClick = onInsertAns
            )
        }

        // Scientific Row 2 (Trigonometry: sin, cos, tan, ln, log)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.95f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val (sinLabel, sinFunc) = when {
                isHyp && isSecond -> "asinh" to "asinh"
                isHyp -> "sinh" to "sinh"
                isSecond -> "sin⁻¹" to "asin"
                else -> "sin" to "sin"
            }
            val (cosLabel, cosFunc) = when {
                isHyp && isSecond -> "acosh" to "acosh"
                isHyp -> "cosh" to "cosh"
                isSecond -> "cos⁻¹" to "acos"
                else -> "cos" to "cos"
            }
            val (tanLabel, tanFunc) = when {
                isHyp && isSecond -> "atanh" to "atanh"
                isHyp -> "tanh" to "tanh"
                isSecond -> "tan⁻¹" to "atan"
                else -> "tan" to "tan"
            }
            val (lnLabel, lnFunc, isLnFunc) = if (isSecond) Triple("eˣ", "exp", true) else Triple("ln", "ln", true)
            val (logLabel, logFunc, isLogFunc) = if (isSecond) Triple("10ˣ", "10^", false) else Triple("log", "log", true)

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = sinLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 14.sp,
                testTag = "btn_sin",
                onClick = { onAppend(sinFunc, true) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = cosLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 14.sp,
                testTag = "btn_cos",
                onClick = { onAppend(cosFunc, true) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = tanLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 14.sp,
                testTag = "btn_tan",
                onClick = { onAppend(tanFunc, true) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = lnLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 14.sp,
                testTag = "btn_ln",
                onClick = { onAppend(lnFunc, isLnFunc) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = logLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 14.sp,
                testTag = "btn_log",
                onClick = { onAppend(logFunc, isLogFunc) }
            )
        }

        // Scientific Row 3 (Roots, Powers, Constants)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.95f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val (rootLabel, rootFunc, isRootFunc) = if (isSecond) Triple("∛", "cbrt", true) else Triple("√", "sqrt", true)
            val (sqrLabel, sqrToken) = if (isSecond) "x³" to "^3" else "x²" to "^2"
            val (powLabel, powToken) = if (isSecond) "ʸ√x" to "root" else "xʸ" to "^"
            val (invLabel, invToken) = if (isSecond) "|x|" to "abs" else "1/x" to "1/"
            val (constLabel, constVal) = if (isSecond) "e" to "e" else "π" to "π"

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = rootLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 16.sp,
                testTag = "btn_root",
                onClick = { onAppend(rootFunc, isRootFunc) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = sqrLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 15.sp,
                testTag = "btn_square",
                onClick = { onAppend(sqrToken, false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = powLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 15.sp,
                testTag = "btn_power",
                onClick = {
                    if (isSecond) onAppend("root", true) else onAppend("^", false)
                }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = invLabel,
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 14.sp,
                testTag = "btn_inv",
                onClick = {
                    if (isSecond) onAppend("abs", true) else onAppend(invToken, false)
                }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = constLabel,
                backgroundColor = KeypadFunction,
                contentColor = ElectricCyan,
                fontSize = 16.sp,
                testTag = "btn_const",
                onClick = { onAppend(constVal, false) }
            )
        }

        // Standard Numbers and Main Operators Grid (4 columns, BIGGER buttons)
        // Row: C, AC, %, ÷
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "C",
                backgroundColor = Color(0xFF331D26),
                contentColor = Color(0xFFF87171),
                fontSize = 19.sp,
                testTag = "btn_clear",
                onClick = onClear
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "AC",
                backgroundColor = Color(0xFF401824),
                contentColor = Color(0xFFFB7185),
                fontSize = 19.sp,
                testTag = "btn_all_clear",
                onClick = onAllClear
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "%",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 20.sp,
                testTag = "btn_percent",
                onClick = { onAppend("%", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "÷",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 26.sp,
                testTag = "btn_divide",
                onClick = { onAppend("÷", false) }
            )
        }

        // Row: 7, 8, 9, ×
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "7",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_7",
                onClick = { onAppend("7", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "8",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_8",
                onClick = { onAppend("8", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "9",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_9",
                onClick = { onAppend("9", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "×",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 26.sp,
                testTag = "btn_multiply",
                onClick = { onAppend("×", false) }
            )
        }

        // Row: 4, 5, 6, −
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "4",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_4",
                onClick = { onAppend("4", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "5",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_5",
                onClick = { onAppend("5", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "6",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_6",
                onClick = { onAppend("6", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "−",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 26.sp,
                testTag = "btn_subtract",
                onClick = { onAppend("−", false) }
            )
        }

        // Row: 1, 2, 3, +
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "1",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_1",
                onClick = { onAppend("1", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "2",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_2",
                onClick = { onAppend("2", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "3",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_3",
                onClick = { onAppend("3", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "+",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 26.sp,
                testTag = "btn_add",
                onClick = { onAppend("+", false) }
            )
        }

        // Row: 0, ., ⌫, =
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "0",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_0",
                onClick = { onAppend("0", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = ".",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 24.sp,
                testTag = "btn_decimal",
                onClick = { onAppend(".", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "⌫",
                icon = Icons.AutoMirrored.Filled.Backspace,
                backgroundColor = Color(0xFF242C3D),
                contentColor = Color(0xFFCBD5E1),
                fontSize = 18.sp,
                testTag = "btn_backspace",
                onClick = onDeleteBack
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "=",
                backgroundColor = Color(0xFF0284C7),
                contentColor = Color.White,
                fontSize = 28.sp,
                testTag = "btn_equals",
                onClick = onCalculate
            )
        }
    }
}

@Composable
private fun BasicKeypadLayout(
    modifier: Modifier = Modifier,
    onAppend: (String, Boolean) -> Unit,
    onDeleteBack: () -> Unit,
    onClear: () -> Unit,
    onAllClear: () -> Unit,
    onCalculate: () -> Unit,
    onInsertAns: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "AC",
                backgroundColor = Color(0xFF401824),
                contentColor = Color(0xFFFB7185),
                fontSize = 22.sp,
                testTag = "basic_btn_ac",
                onClick = onAllClear
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "( )",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 20.sp,
                testTag = "basic_btn_parens",
                onClick = { onAppend("(", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "%",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 22.sp,
                testTag = "basic_btn_percent",
                onClick = { onAppend("%", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "÷",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 28.sp,
                testTag = "basic_btn_divide",
                onClick = { onAppend("÷", false) }
            )
        }

        // Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "7",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_7",
                onClick = { onAppend("7", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "8",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_8",
                onClick = { onAppend("8", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "9",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_9",
                onClick = { onAppend("9", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "×",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 28.sp,
                testTag = "basic_btn_multiply",
                onClick = { onAppend("×", false) }
            )
        }

        // Row 3
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "4",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_4",
                onClick = { onAppend("4", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "5",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_5",
                onClick = { onAppend("5", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "6",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_6",
                onClick = { onAppend("6", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "−",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 28.sp,
                testTag = "basic_btn_subtract",
                onClick = { onAppend("−", false) }
            )
        }

        // Row 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "1",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_1",
                onClick = { onAppend("1", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "2",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_2",
                onClick = { onAppend("2", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "3",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_3",
                onClick = { onAppend("3", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "+",
                backgroundColor = KeypadOperator,
                contentColor = KeypadOperatorText,
                fontSize = 28.sp,
                testTag = "basic_btn_add",
                onClick = { onAppend("+", false) }
            )
        }

        // Row 5
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.05f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "0",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_0",
                onClick = { onAppend("0", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = ".",
                backgroundColor = KeypadNumber,
                contentColor = KeypadNumberText,
                fontSize = 26.sp,
                testTag = "basic_btn_decimal",
                onClick = { onAppend(".", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "⌫",
                icon = Icons.AutoMirrored.Filled.Backspace,
                backgroundColor = Color(0xFF242C3D),
                contentColor = Color(0xFFCBD5E1),
                fontSize = 22.sp,
                testTag = "basic_btn_backspace",
                onClick = onDeleteBack
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "=",
                backgroundColor = Color(0xFF0284C7),
                contentColor = Color.White,
                fontSize = 30.sp,
                testTag = "basic_btn_equals",
                onClick = onCalculate
            )
        }
    }
}

@Composable
private fun ConstantsAndMemoryLayout(
    modifier: Modifier = Modifier,
    onAppend: (String, Boolean) -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySub: () -> Unit,
    onMemoryStore: () -> Unit,
    onClear: () -> Unit,
    onCalculate: () -> Unit,
    onDeleteBack: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Section Title: Memoria
        Text(
            text = "REGISTROS DE MEMORIA",
            fontFamily = InterFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(start = 6.dp, top = 2.dp)
        )

        // Memory row: MC, MR, M+, M-, MS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "MC",
                backgroundColor = Color(0xFF331D26),
                contentColor = Color(0xFFF43F5E),
                fontSize = 15.sp,
                testTag = "btn_mc",
                onClick = onMemoryClear
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "MR",
                backgroundColor = Color(0xFF1E283A),
                contentColor = ElectricCyan,
                fontSize = 15.sp,
                testTag = "btn_mr",
                onClick = onMemoryRecall
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "M+",
                backgroundColor = Color(0xFF1E283A),
                contentColor = Color(0xFF38BDF8),
                fontSize = 15.sp,
                testTag = "btn_m_plus",
                onClick = onMemoryAdd
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "M−",
                backgroundColor = Color(0xFF1E283A),
                contentColor = Color(0xFF38BDF8),
                fontSize = 15.sp,
                testTag = "btn_m_minus",
                onClick = onMemorySub
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "MS",
                backgroundColor = Color(0xFF1E283A),
                contentColor = Color(0xFFA78BFA),
                fontSize = 15.sp,
                testTag = "btn_ms",
                onClick = onMemoryStore
            )
        }

        // Section Title: Constantes Universales
        Text(
            text = "CONSTANTES Y COMBINATORIA",
            fontFamily = InterFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(start = 6.dp, top = 2.dp)
        )

        // Constants row: π, e, φ, n!, nCr
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "π",
                secondaryLabel = "3.14159",
                backgroundColor = KeypadFunction,
                contentColor = ElectricCyan,
                fontSize = 18.sp,
                testTag = "btn_pi",
                onClick = { onAppend("π", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "e",
                secondaryLabel = "2.71828",
                backgroundColor = KeypadFunction,
                contentColor = ElectricCyan,
                fontSize = 18.sp,
                testTag = "btn_e",
                onClick = { onAppend("e", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "φ",
                secondaryLabel = "1.61803",
                backgroundColor = KeypadFunction,
                contentColor = ElectricCyan,
                fontSize = 18.sp,
                testTag = "btn_phi",
                onClick = { onAppend("φ", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "n!",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 16.sp,
                testTag = "btn_fact",
                onClick = { onAppend("!", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "nCr",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 15.sp,
                testTag = "btn_ncr",
                onClick = { onAppend("nCr", true) }
            )
        }

        // Advanced Math row: nPr, mod, abs, gcd, lcm
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "nPr",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 15.sp,
                testTag = "btn_npr",
                onClick = { onAppend("nPr", true) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "mod",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 15.sp,
                testTag = "btn_mod",
                onClick = { onAppend("mod", false) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "|x|",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 16.sp,
                testTag = "btn_abs",
                onClick = { onAppend("abs", true) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "mcd",
                secondaryLabel = "gcd",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 15.sp,
                testTag = "btn_gcd",
                onClick = { onAppend("gcd", true) }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "mcm",
                secondaryLabel = "lcm",
                backgroundColor = KeypadFunction,
                contentColor = KeypadFunctionText,
                fontSize = 15.sp,
                testTag = "btn_lcm",
                onClick = { onAppend("lcm", true) }
            )
        }

        // Quick bottom action row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "C",
                backgroundColor = Color(0xFF331D26),
                contentColor = Color(0xFFF87171),
                fontSize = 18.sp,
                testTag = "const_btn_c",
                onClick = onClear
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                label = "⌫",
                icon = Icons.AutoMirrored.Filled.Backspace,
                backgroundColor = Color(0xFF242C3D),
                contentColor = Color(0xFFCBD5E1),
                fontSize = 18.sp,
                testTag = "const_btn_backspace",
                onClick = onDeleteBack
            )
            CalculatorButton(
                modifier = Modifier.weight(2f).fillMaxHeight(),
                label = "=",
                backgroundColor = Color(0xFF0284C7),
                contentColor = Color.White,
                fontSize = 26.sp,
                testTag = "const_btn_equals",
                onClick = onCalculate
            )
        }
    }
}
