package com.purkt.ui.presentation.button.ui.component

import android.content.res.Configuration
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
    isReadOnly: Boolean = false,
    isError: Boolean = false,
) {
    val baseColors = if (isError) {
        TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.error
        )
    } else TextFieldDefaults.textFieldColors(
        focusedLabelColor = MaterialTheme.colors.secondary
    )
    val interactionSource = remember { MutableInteractionSource() }
    val baseLabelColor = baseColors.labelColor(
        enabled = true,
        error = isError,
        interactionSource = interactionSource
    ).value
    val readOnlyColors = TextFieldDefaults.textFieldColors(
        disabledTextColor = baseColors.textColor(enabled = true).value,
        disabledLabelColor = baseLabelColor,
        unfocusedLabelColor = baseLabelColor
    )
    TextField(
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
        colors = if (isReadOnly) readOnlyColors else baseColors,
        isError = isError
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
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
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewNormalEditTextReadOnly() {
    val inputState = rememberSaveable { mutableStateOf("This is title") }
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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewNormalEditTextError() {
    val inputState = rememberSaveable { mutableStateOf("This is title") }
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
                isError = true
            )
        }
    }
}
