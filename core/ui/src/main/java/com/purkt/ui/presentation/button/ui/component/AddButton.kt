package com.purkt.ui.presentation.button.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.ui.R

/**
 * An button with the add icon in front of button text.
 * @param modifier The modifier of this view.
 * @param text The target text to show in the button.
 * @param color The color of this button.
 * @param onClick The function to invoke when click this button.
 */
@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colors.primary,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = Modifier
            .then(modifier),
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color
        )
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp),
            painter = painterResource(id = R.drawable.ic_plus_white),
            tint = Color.White,
            contentDescription = "Add icon in AddButton"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Preview
@Composable
private fun PreviewAddButton() {
    AddButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Test button"
    )
}
