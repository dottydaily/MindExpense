package com.purkt.mindexpense.expense.presentation.screen.list.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.database.domain.model.Expense
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseCardInfoState
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ExpenseCardInfo(
    state: ExpenseCardInfoState,
    onExpandCard: (ExpenseCardInfoState) -> Unit = {},
    onDeleteCard: () -> Unit = {}
) {
    val expense = state.expense
    val isExpanded = state.isExpanded
    val backgroundColor = if (isExpanded) {
        MaterialTheme.colors.surface
    } else {
        MaterialTheme.colors.primaryVariant
    }
    val contentColor = if (isExpanded) {
        MaterialTheme.colors.primaryVariant
    } else {
        contentColorFor(backgroundColor)
    }
    val border = if (isExpanded) {
        BorderStroke(2.dp, contentColor)
    } else {
        BorderStroke(0.dp, contentColor)
    }
    val shape = if (isExpanded) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    } else {
        RoundedCornerShape(16.dp)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                border = border,
                shape = shape
            ),
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val maxLinesTitle = if (state.isExpanded) Int.MAX_VALUE else 1
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = expense.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = maxLinesTitle,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val amountFormatted = DecimalFormat("#,##0.00").format(expense.amount)
                    val currencyDisplayName = expense.currency.currencyCode
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM, dd, yyyy HH:mm")
                    val dateTimeString = dateTimeFormatter.format(expense.dateTime)
                    Text(
                        text = "$amountFormatted $currencyDisplayName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = dateTimeString,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = state.expense.description
                    )
                }
            }
            Row {
                if (isExpanded) {
                    TextButton(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        onClick = {
                            onDeleteCard.invoke()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.error
                        )
                    ) {
                        Text(text = "Delete")
                    }
                }
                TextButton(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    onClick = {
                        onExpandCard.invoke(state)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = contentColor
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
}

@Preview
@Composable
private fun PreviewExpenseCardInfoCollapse() {
    val state = ExpenseCardInfoState(
        Expense(
            title = "LunchLunchLunchLunchLunch",
            description = "Eat lunch with friend at the mall",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        isExpanded = false
    )
    MindExpenseTheme {
        ExpenseCardInfo(state = state)
    }
}

@Preview
@Composable
private fun PreviewExpenseCardInfoExpanded() {
    val state = ExpenseCardInfoState(
        Expense(
            title = "LunchLunchLunchLunchLunch",
            description = "Eat lunch with friend at the mall",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        isExpanded = true
    )
    MindExpenseTheme {
        ExpenseCardInfo(state = state)
    }
}
