package com.purkt.ui.presentation.button.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme

@Composable
fun NumberEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = ""
) {
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
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}

@Preview
@Composable
private fun PreviewNumberEditText() {
    val inputState = rememberSaveable { mutableStateOf("") }
    MindExpenseTheme {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            NumberEditText(
                modifier = Modifier.padding(8.dp),
                value = inputState.value,
                onValueChange = { inputState.value += it },
                label = "Amount",
                placeholder = "Enter your expense's amount"
            )
        }
    }
}