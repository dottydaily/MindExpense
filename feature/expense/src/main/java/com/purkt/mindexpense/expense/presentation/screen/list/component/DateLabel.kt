package com.purkt.mindexpense.expense.presentation.screen.list.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateLabel(
    modifier: Modifier = Modifier,
    dateString: String
) {
    Card(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(50),
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Text(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            text = dateString,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onPrimary
        )
    }
}

@Preview
@Composable
fun PreviewDateLabel() {
    Surface(color = Color.White) {
        DateLabel(dateString = DateTimeFormatter.ofPattern("dd MMM yyyy").format(LocalDate.now()))
    }
}
