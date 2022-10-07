package com.purkt.mindexpense.expense.presentation.screen.list.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.database.domain.model.Expense
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ExpenseCardInfo(
    cardDetail: ExpenseInfoItem.ExpenseCardDetail,
    onDeleteCard: () -> Unit = {}
) {
    val expense = cardDetail.expense
    val isExpanded = cardDetail.isExpanded
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
        MaterialTheme.shapes.medium
    } else {
        RoundedCornerShape(
            bottomEndPercent = 25
        )
    }

    val interactionSource = MutableInteractionSource()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                border = border,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = { cardDetail.isExpanded = true }
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
                    bottom = 16.dp
                ),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val maxLinesTitle = if (cardDetail.isExpanded) Int.MAX_VALUE else 1
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = expense.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = maxLinesTitle,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = cardDetail.expense.description,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val amountFormatted = DecimalFormat("#,##0.00").format(expense.amount)
                    val currencyDisplayName = expense.currency.currencyCode
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
                    val dateTimeString = "Time : ${dateTimeFormatter.format(expense.dateTime)}"
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
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
            ) {
                if (isExpanded) {
                    TextButton(
                        onClick = {
                            onDeleteCard.invoke()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.error
                        )
                    ) {
                        Text(text = "Delete")
                    }
                    TextButton(
                        onClick = {
                            cardDetail.isExpanded = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = contentColor
                        )
                    ) {
                        Text(text = "Hide info")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewExpenseCardInfoCollapse() {
    val cardDetail = ExpenseInfoItem.ExpenseCardDetail(
        Expense(
            title = "LunchLunchLunchLunchLunch",
            description = "Eat lunch with friend at the mall near my home",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        isExpanded = false
    )
    MindExpenseTheme {
        ExpenseCardInfo(cardDetail = cardDetail)
    }
}

@Preview
@Composable
private fun PreviewExpenseCardInfoExpanded() {
    val cardDetail = ExpenseInfoItem.ExpenseCardDetail(
        Expense(
            title = "LunchLunchLunchLunchLunch",
            description = "Eat lunch with friend at the mall near my home",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        isExpanded = true
    )
    MindExpenseTheme {
        ExpenseCardInfo(cardDetail = cardDetail)
    }
}
