package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ads.AdBanner
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.CalculatorHeader
import com.example.ui.components.CalculatorKeypad
import com.example.ui.components.FullScreenCalculatorView
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.theme.DeepObsidian

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onToolUsed: ((Boolean) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isHistoryOpen by remember { mutableStateOf(false) }
    var isFullScreenOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle temporary status toasts
    LaunchedEffect(uiState.statusToast) {
        uiState.statusToast?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusToast()
        }
    }

    if (isFullScreenOpen) {
        BackHandler { isFullScreenOpen = false }
        FullScreenCalculatorView(
            initialExpression = uiState.expression,
            angleUnit = uiState.angleUnit,
            onClose = { isFullScreenOpen = false },
            onApplyToMain = { expr, res ->
                viewModel.onSetExpressionAndResult(expr, res)
            },
            onCopyResult = { text -> viewModel.copyToClipboard(context, text) }
        )
    } else {
        Scaffold(
            containerColor = DeepObsidian,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                // Banner docked at the very bottom ("banner debajo")
                AdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 2.dp)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DeepObsidian)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    // Header with status pills, angle toggle, history, and full-screen editor
                    CalculatorHeader(
                        angleUnit = uiState.angleUnit,
                        isSecondFunction = uiState.isSecondFunction,
                        isHyperbolic = uiState.isHyperbolic,
                        hasMemory = uiState.hasMemory,
                        memoryValue = uiState.memoryValue,
                        historyCount = uiState.history.size,
                        onToggleAngleUnit = {
                            viewModel.onToggleAngleUnit()
                            onToolUsed?.invoke(false)
                        },
                        onOpenHistory = {
                            isHistoryOpen = true
                            onToolUsed?.invoke(true)
                        },
                        onOpenFullScreen = {
                            isFullScreenOpen = true
                            onToolUsed?.invoke(false)
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Enlarged modern scrolling Display area with bigger fonts
                    CalculatorDisplay(
                        expression = uiState.expression,
                        result = uiState.result,
                        previewResult = uiState.previewResult,
                        fractionResult = uiState.fractionResult,
                        errorMessage = uiState.errorMessage,
                        onCopyResult = { text -> viewModel.copyToClipboard(context, text) },
                        onDeleteBack = viewModel::onDeleteBack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Enlarged and organized keypad taking full remaining height
                    CalculatorKeypad(
                        modifier = Modifier.weight(1f),
                        activeCategory = uiState.activeCategory,
                        isSecondFunction = uiState.isSecondFunction,
                        isHyperbolic = uiState.isHyperbolic,
                        onSelectCategory = { cat ->
                            viewModel.setActiveCategory(cat)
                            onToolUsed?.invoke(false)
                        },
                        onAppendToken = viewModel::onAppendText,
                        onDeleteBack = viewModel::onDeleteBack,
                        onClear = viewModel::onClear,
                        onAllClear = viewModel::onAllClear,
                        onCalculate = {
                            viewModel.onCalculate()
                            onToolUsed?.invoke(false)
                        },
                        onToggleSecond = viewModel::onToggleSecond,
                        onToggleHyp = viewModel::onToggleHyperbolic,
                        onMemoryClear = viewModel::onMemoryClear,
                        onMemoryRecall = viewModel::onMemoryRecall,
                        onMemoryAdd = {
                            viewModel.onMemoryAdd()
                            onToolUsed?.invoke(false)
                        },
                        onMemorySub = {
                            viewModel.onMemorySub()
                            onToolUsed?.invoke(false)
                        },
                        onMemoryStore = {
                            viewModel.onMemoryStore()
                            onToolUsed?.invoke(false)
                        },
                        onInsertAns = viewModel::onInsertLastAnswer
                    )
                }

                // History Bottom Sheet
                if (isHistoryOpen) {
                    HistoryBottomSheet(
                        sheetState = sheetState,
                        history = uiState.history,
                        onDismiss = { isHistoryOpen = false },
                        onUseExpression = { item ->
                            viewModel.onUseHistoryItem(item, useResult = false)
                            isHistoryOpen = false
                        },
                        onUseResult = { item ->
                            viewModel.onUseHistoryItem(item, useResult = true)
                            isHistoryOpen = false
                        },
                        onCopyText = { text -> viewModel.copyToClipboard(context, text) },
                        onDeleteItem = viewModel::onDeleteHistoryItem,
                        onClearAll = viewModel::onClearHistory
                    )
                }
            }
        }
    }
}
