package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.JetBrainsMono

@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    label: String = "",
    secondaryLabel: String? = null,
    icon: ImageVector? = null,
    backgroundColor: Color,
    contentColor: Color,
    isActive: Boolean = false,
    fontSize: TextUnit = 20.sp,
    testTag: String = label,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = if (isPressed) 0.94f else 1.0f

    val effectiveBg = if (isActive) {
        backgroundColor.copy(alpha = 0.95f)
    } else {
        backgroundColor
    }

    val borderColor = if (isActive) {
        ElectricCyan.copy(alpha = 0.85f)
    } else if (isPressed) {
        contentColor.copy(alpha = 0.5f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(effectiveBg)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = contentColor.copy(alpha = 0.35f)),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(2.dp)
        ) {
            if (secondaryLabel != null) {
                Text(
                    text = secondaryLabel,
                    color = contentColor.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMono,
                    lineHeight = 11.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JetBrainsMono,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
