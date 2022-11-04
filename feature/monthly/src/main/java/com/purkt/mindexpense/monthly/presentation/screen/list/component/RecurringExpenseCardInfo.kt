package com.purkt.mindexpense.monthly.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.mindexpense.monthly.presentation.screen.list.state.RecurringExpenseInfoItem
import com.purkt.model.domain.model.RecurringExpense
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalTime
import java.util.Currency

@Composable
fun RecurringExpenseCardInfo(
    modifier: Modifier = Modifier,
    cardInfo: RecurringExpenseInfoItem,
    onDeleteListener: (expense: RecurringExpense) -> Unit = {},
    onEditListener: (recurringExpenseId: Int) -> Unit = {}
) {
    val expense = cardInfo.recurringExpense
    var isExpanded = cardInfo.isExpanded

    val backgroundColor = MaterialTheme.colors.primary
    val contentColor = contentColorFor(backgroundColor = backgroundColor)
    val interactionSource = MutableInteractionSource()
    Card(
        modifier = Modifier
            .animateContentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = { isExpanded = !isExpanded }
            )
            .then(modifier),
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(10)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = expense.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = expense.description,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    val amountText = "${expense.currency.currencyCode} ${expense.amount}"
                    val dateDetailText = when (expense.dayOfMonth) {
                        1, 21 -> "${expense.dayOfMonth}st each Month"
                        2, 22 -> "${expense.dayOfMonth}nd each Month"
                        3, 13, 23 -> "${expense.dayOfMonth}rd each Month"
                        else -> "${expense.dayOfMonth}th each Month"
                    }
                    Text(
                        text = amountText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateDetailText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                            onDeleteListener.invoke(cardInfo.recurringExpense)
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
                            onEditListener.invoke(cardInfo.recurringExpense.id)
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
fun PreviewRecurringExpenseCardInfo() {
    val mockExpense = RecurringExpense(
        title = "Youtube Premium",
        description = "Family sharing w/ Time Bank Jun",
        amount = 209.00,
        currency = Currency.getInstance("THB"),
        dayOfMonth = 14,
        time = LocalTime.now()
    )
    MindExpenseTheme {
        Surface {
            RecurringExpenseCardInfo(
                cardInfo = RecurringExpenseInfoItem(
                    recurringExpense = mockExpense,
                    isExpanded = false
                )
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRecurringExpenseCardInfoExpanded() {
    val mockExpense = RecurringExpense(
        title = "Youtube Premium",
        description = "Family sharing w/ Time Bank Jun",
        amount = 209.00,
        currency = Currency.getInstance("THB"),
        dayOfMonth = 14,
        time = LocalTime.now()
    )
    MindExpenseTheme {
        Surface {
            RecurringExpenseCardInfo(
                cardInfo = RecurringExpenseInfoItem(
                    recurringExpense = mockExpense,
                    isExpanded = true
                )
            )
        }
    }
}
