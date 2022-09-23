package com.purkt.mindexpense.expense.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.purkt.ui.presentation.button.ui.component.AddButton
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import com.purkt.mindexpense.expense.domain.model.state.ExpenseCardInfoState
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ExpenseListPage(
    cardInfoList: List<ExpenseCardInfoState>,
    onNavigateToAddExpensePage: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val totalAmount = DecimalFormat("#,##0.00").format(cardInfoList.sumOf { it.amount })
            val currency = cardInfoList.firstOrNull()?.currency?.currencyCode ?: ""
            Text(
                modifier = Modifier
                    .padding(
                        vertical = 16.dp
                    )
                    .align(Alignment.CenterHorizontally),
                text = "$totalAmount $currency",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cardInfoList) {
                    ExpenseCardInfo(state = it)
                }
            }
            AddButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Add new expense",
                onClick = onNavigateToAddExpensePage
            )
        }
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
                    val currencyDisplayName = state.currency.currencyCode
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM, dd, yyyy HH:mm")
                    val dateTimeString = dateTimeFormatter.format(state.dateTime)
                    Text(
                        text = "$amountFormatted $currencyDisplayName",
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

@Preview
@Composable
private fun PreviewExpenseScreenPage() {
    val data = listOf(
        ExpenseCardInfoState(
            title = "Lunch",
            description = "Eat lunch with friend at the mall",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now(),
            isExpanded = false
        ),
        ExpenseCardInfoState(
            title = "Lunch",
            description = "Eat lunch with friend at the mall",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now(),
            isExpanded = false
        ),
        ExpenseCardInfoState(
            title = "Lunch",
            description = "Eat lunch with friend at the mall",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now(),
            isExpanded = false
        )
    )
    ExpenseListPage(data)
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
    MindExpenseTheme {
        ExpenseCardInfo(state = state)
    }
}
