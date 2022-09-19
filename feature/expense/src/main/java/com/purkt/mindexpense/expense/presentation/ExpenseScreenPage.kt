package com.purkt.mindexpense.expense.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.mindexpense.expense.domain.model.state.ExpenseCardInfoState
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ExpenseScreenPage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colors.background)
    ) {
    }
}

@Composable
private fun ExpenseCardInfo(state: ExpenseCardInfoState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val maxLinesTitle = if (state.isExpanded) Int.MAX_VALUE else 1
                Text(
                    text = state.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = maxLinesTitle
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    val amountFormatted = DecimalFormat("#,##0.00").format(state.amount)
                    val amountString = state.currency.symbol
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM, dd, yyyy HH:mm")
                    val dateTimeString = dateTimeFormatter.format(state.dateTime)
                    Text(
                        text = "$amountString$amountFormatted",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateTimeString,
                        fontSize = 10.sp
                    )
                }
            }
            if (state.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = state.description
                    )
                }
            }
            TextButton(
                onClick = { state.isExpanded = !state.isExpanded },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                val content = if (state.isExpanded) "Hide info" else "Show info"
                Text(
                    text = content
                )
            }
        }
    }
}

// @Preview
@Composable
private fun PreviewExpenseScreenPage() {
    ExpenseScreenPage()
}

@Preview
@Composable
private fun PreviewExpenseCardInfoCollapse() {
    val state = ExpenseCardInfoState(
        title = "Lunch",
        description = "Eat lunch with friend at the mall",
        amount = 699.00,
        currency = Currency.getInstance("THB"),
        dateTime = LocalDateTime.now(),
        isExpanded = false
    )
    ExpenseCardInfo(state = state)
}
