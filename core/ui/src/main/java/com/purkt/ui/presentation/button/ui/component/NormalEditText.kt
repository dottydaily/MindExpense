package com.purkt.ui.presentation.button.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme

@Composable
fun NormalEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    isReadOnly: Boolean = false
) {
    val baseColors = TextFieldDefaults.outlinedTextFieldColors()
    val interactionSource = remember { MutableInteractionSource() }
    val baseLabelColor = baseColors.labelColor(
        enabled = true,
        error = false,
        interactionSource = interactionSource
    ).value
    val readOnlyColors = TextFieldDefaults.outlinedTextFieldColors(
        disabledTextColor = baseColors.textColor(enabled = true).value,
        disabledLabelColor = baseLabelColor,
        unfocusedLabelColor = baseLabelColor,

    )
    OutlinedTextField(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(percent = 10),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(
                text = placeholder
            )
        },
        readOnly = isReadOnly,
        enabled = !isReadOnly,
        colors = if (isReadOnly) readOnlyColors else baseColors
    )
}

@Preview
@Composable
private fun PreviewNormalEditText() {
    val inputState = rememberSaveable { mutableStateOf("") }
    MindExpenseTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            NormalEditText(
                modifier = Modifier.padding(8.dp),
                value = inputState.value,
                onValueChange = { inputState.value += it },
                label = "Title",
                placeholder = "Enter your expense's title"
            )
        }
    }
}

@Preview
@Composable
private fun PreviewNormalEditTextReadOnly() {
    val inputState = rememberSaveable { mutableStateOf("") }
    MindExpenseTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            NormalEditText(
                modifier = Modifier.padding(8.dp),
                value = inputState.value,
                onValueChange = { inputState.value += it },
                label = "Title",
                placeholder = "Enter your expense's title",
                isReadOnly = true
            )
        }
    }
}
