package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import kotlin.math.abs
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.AngleUnit
import com.example.math.BigNumberEngine
import com.example.math.FractionHelper
import com.example.math.MathEngine
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.InterFont
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.NeonSky

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FullScreenCalculatorView(
    initialExpression: String = "",
    angleUnit: AngleUnit = AngleUnit.DEG,
    onClose: () -> Unit,
    onApplyToMain: (String, String) -> Unit,
    onCopyResult: (String) -> Unit
) {
    var expression by remember { mutableStateOf(initialExpression) }
    var resultText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isHighPrecisionMode by remember { mutableStateOf(true) }
    var fractionText by remember { mutableStateOf<String?>(null) }

    fun calculateNow() {
        if (expression.isBlank()) {
            resultText = "0"
            errorText = null
            fractionText = null
            return
        }

        try {
            // First check if formula contains large factorials like 30!, 100!
            val trimmed = expression.trim()
            val factMatch = Regex("""^(\d+)!$""").find(trimmed)
            if (factMatch != null) {
                val n = factMatch.groupValues[1].toLongOrNull()
                if (n != null && n in 0..5000) {
                    val bigFact = BigNumberEngine.factorial(n)
                    val str = bigFact.toString()
                    resultText = str
                    errorText = null
                    fractionText = null
                    return
                }
            }

            // Normal and advanced evaluation
            val engine = MathEngine(angleUnit)
            val evalDouble = engine.evaluate(expression)

            val formatted = if (isHighPrecisionMode) {
                // If clean integer or exact power, format with full digits
                if (abs(evalDouble - evalDouble.toLong()) < 1e-11 && abs(evalDouble) < 1e16) {
                    evalDouble.toLong().toString()
                } else {
                    MathEngine.formatResult(evalDouble)
                }
            } else {
                MathEngine.formatResult(evalDouble)
            }

            resultText = formatted
            fractionText = FractionHelper.toFractionString(evalDouble)
            errorText = null
        } catch (e: Exception) {
            errorText = e.message ?: "Error de cálculo"
            resultText = ""
            fractionText = null
        }
    }

    // Initial evaluation if expression is provided
    remember(initialExpression) {
        if (initialExpression.isNotBlank()) calculateNow()
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DeepObsidian,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Modo Pantalla Completa",
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Alta precisión & números grandes",
                            fontFamily = InterFont,
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Precision Mode toggle pill
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                isHighPrecisionMode = !isHighPrecisionMode
                                calculateNow()
                            }
                            .padding(end = 8.dp),
                        color = if (isHighPrecisionMode) Color(0xFF1B2F44) else Color(0xFF1E2430),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isHighPrecisionMode) ElectricCyan.copy(alpha = 0.6f) else Color(0xFF334155)
                        )
                    ) {
                        Text(
                            text = if (isHighPrecisionMode) "128-bit Alta Precisión" else "Estándar",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHighPrecisionMode) ElectricCyan else Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepObsidian
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TextField for expression input
            Text(
                text = "ENTRADA DE FÓRMULA (TEXTFIELD)",
                fontFamily = InterFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            OutlinedTextField(
                value = expression,
                onValueChange = {
                    expression = it
                    calculateNow()
                },
                placeholder = {
                    Text(
                        text = "Introduce tu expresión (ej: 100!, 2^64, sin(45) + ln(e)...)",
                        fontFamily = JetBrainsMono,
                        fontSize = 15.sp,
                        color = Color(0xFF64748B)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fullscreen_input_textfield"),
                textStyle = TextStyle(
                    fontFamily = JetBrainsMono,
                    fontSize = 20.sp,
                    color = Color.White,
                    lineHeight = 28.sp
                ),
                minLines = 3,
                maxLines = 6,
                trailingIcon = {
                    if (expression.isNotEmpty()) {
                        IconButton(onClick = {
                            expression = ""
                            resultText = ""
                            errorText = null
                            fractionText = null
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar fórmula",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { calculateNow() }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF131924),
                    unfocusedContainerColor = Color(0xFF10151E),
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = Color(0xFF253347),
                    cursorColor = ElectricCyan
                )
            )

            // Quick Operator Chips Toolbar
            Text(
                text = "INSERCIÓN RÁPIDA DE SÍMBOLOS",
                fontFamily = InterFont,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            val symbols = listOf(
                "+", "-", "×", "÷", "^", "√", "!", "(", ")",
                "π", "e", "φ", "sin", "cos", "tan", "ln", "log", "mod", "abs"
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                symbols.forEach { sym ->
                    val isFunc = sym in listOf("sin", "cos", "tan", "ln", "log", "abs", "√")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1C2536))
                            .border(1.dp, Color(0xFF2C3B50), RoundedCornerShape(8.dp))
                            .clickable {
                                expression += if (isFunc && sym == "√") "√(" else if (isFunc) "$sym(" else sym
                                calculateNow()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sym,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (isFunc) ElectricCyan else Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            // Calculate Button
            Button(
                onClick = { calculateNow() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("fullscreen_calculate_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calcular con Alta Precisión",
                    fontFamily = InterFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Result Display Card with Text
            Text(
                text = "RESULTADO DE ALTA PRECISIÓN (TEXT)",
                fontFamily = InterFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF121722)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF223044))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (resultText.isNotEmpty() && errorText == null) {
                                val digitCount = resultText.filter { it.isDigit() }.length
                                if (digitCount > 15) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1E2B3E))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "$digitCount dígitos",
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = ElectricCyan
                                        )
                                    }
                                }
                            }
                            if (fractionText != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "≈ $fractionText",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 12.sp,
                                    color = NeonSky
                                )
                            }
                        }

                        if (resultText.isNotEmpty()) {
                            IconButton(
                                onClick = { onCopyResult(resultText) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar resultado",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (errorText != null) {
                        Text(
                            text = errorText ?: "Error",
                            fontFamily = JetBrainsMono,
                            fontSize = 16.sp,
                            color = Color(0xFFF87171)
                        )
                    } else {
                        // The primary Text for displaying large high precision results
                        Text(
                            text = if (resultText.isEmpty()) "El resultado aparecerá aquí..." else resultText,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (resultText.length > 30) 22.sp else 30.sp,
                            color = if (resultText.isEmpty()) Color(0xFF475569) else Color.White,
                            lineHeight = if (resultText.length > 30) 30.sp else 38.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fullscreen_result_text")
                        )
                    }

                    if (resultText.isNotEmpty() && errorText == null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onApplyToMain(expression, resultText)
                                onClose()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E293B),
                                contentColor = ElectricCyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Transferir a la Calculadora Principal",
                                fontFamily = InterFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
