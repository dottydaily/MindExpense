package com.purkt.mindexpense.expense.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
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
        backgroundColor = MaterialTheme.colors.secondary
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
            color = MaterialTheme.colors.onSecondary
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDateLabel() {
    MindExpenseTheme {
        Surface {
            DateLabel(dateString = DateTimeFormatter.ofPattern("eeee, MMMM d, yyyy").format(LocalDate.now()))
        }
    }
}
