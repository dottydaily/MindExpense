package com.purkt.mindexpense.expense.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import com.purkt.model.domain.model.IndividualExpense
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ExpenseCardInfo(
    cardDetail: ExpenseInfoItem.ExpenseCardDetail,
    onEditExpense: (expenseId: Int) -> Unit = {},
    onDeleteCard: (com.purkt.model.domain.model.IndividualExpense) -> Unit = {}
) {
    val expense = cardDetail.expense
    val isExpanded = cardDetail.isExpanded
    val backgroundColor = MaterialTheme.colors.surface
    val contentColor = MaterialTheme.colors.onSurface

    val interactionSource = MutableInteractionSource()
    Box(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .wrapContentHeight()
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = { cardDetail.isExpanded = !cardDetail.isExpanded }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
            ) {
                val maxLinesTitle = if (cardDetail.isExpanded) Int.MAX_VALUE else 1
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = expense.title,
                        color = contentColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = maxLinesTitle,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = cardDetail.expense.description,
                        color = contentColor,
                        fontSize = 14.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val amountFormatted = DecimalFormat("#,##0.00").format(expense.amount)
                    val currencyDisplayName = expense.currency.currencyCode
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
                    val dateTimeString = "Time : ${dateTimeFormatter.format(expense.dateTime)}"
                    Text(
                        text = "$currencyDisplayName $amountFormatted",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = dateTimeString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                            onDeleteCard.invoke(expense)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.error
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(18.dp),
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove icon for button"
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Remove")
                    }
                    TextButton(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                            onEditExpense.invoke(expense.id)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = contentColor
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(18.dp),
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit icon for button"
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Edit")
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseCardInfoCollapse() {
    val cardDetail = ExpenseInfoItem.ExpenseCardDetail(
        IndividualExpense(
            title = "LunchLunchLunchLunchLunch",
            description = "Eat lunch with friend at the mall near my home",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        isExpanded = false
    )
    MindExpenseTheme {
        Surface {
            ExpenseCardInfo(cardDetail = cardDetail)
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseCardInfoExpanded() {
    val cardDetail = ExpenseInfoItem.ExpenseCardDetail(
        IndividualExpense(
            title = "LunchLunchLunchLunchLunch",
            description = "Eat lunch with friend at the mall near my home",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        isExpanded = true
    )
    MindExpenseTheme {
        Surface {
            ExpenseCardInfo(cardDetail = cardDetail)
        }
    }
}
