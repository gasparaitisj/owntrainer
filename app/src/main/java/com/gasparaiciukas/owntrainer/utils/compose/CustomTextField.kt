package com.gasparaiciukas.owntrainer.utils.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholderText: (@Composable () -> Unit)? = null,
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    cursorBrush: SolidColor = SolidColor(MaterialTheme.colorScheme.primary),
    textStyle: TextStyle = LocalTextStyle.current.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = fontSize,
    ),
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = cursorBrush,
        textStyle = textStyle,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.border(
                    0.5.dp,
                    Color.Gray,
                    RoundedCornerShape(percent = 50),
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                ) {
                    if (value.isEmpty()) placeholderText?.invoke()
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        },
    )
}
