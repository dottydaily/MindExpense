package com.purkt.mindexpense.monthly.presentation.screen.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.mindexpense.monthly.presentation.screen.list.component.RecurringExpenseCardInfo
import com.purkt.mindexpense.monthly.presentation.screen.list.state.RecurringExpenseInfoItem
import com.purkt.model.domain.model.RecurringExpense
import com.purkt.ui.presentation.button.ui.component.TotalAmountBox
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalTime
import java.util.*

@Composable
fun MonthlyListPage() {
    BaseMonthlyListPage(
        cardInfoItems = emptyList()
    )
}

@Composable
private fun BaseMonthlyListPage(
    isLoading: Boolean = false,
    cardInfoItems: List<RecurringExpenseInfoItem>,
    totalAmount: Double = 0.0,
    currency: Currency = Currency.getInstance(Locale.getDefault()),
    onAddExpenseListener: () -> Unit = {},
    onEditExpenseListener: (recurringExpenseId: Int) -> Unit = {},
    onDeleteExpenseListener: (expense: RecurringExpense) -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { onAddExpenseListener.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add icon for Monthly List page"
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colors.primary)
                    ) {
                        TotalAmountBox(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            totalAmount = totalAmount,
                            currency = currency.currencyCode
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = cardInfoItems,
                            key = { it.recurringExpense.id }
                        ) {
                            RecurringExpenseCardInfo(
                                cardInfo = it,
                                onEditListener = onEditExpenseListener,
                                onDeleteListener = onDeleteExpenseListener
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMonthlyListPage() {
    val mockItems = listOf(
        RecurringExpenseInfoItem(
            recurringExpense = RecurringExpense(
                id = 1,
                title = "Youtube Premium",
                description = "Shared with friends",
                amount = 209.00,
                currency = Currency.getInstance("THB"),
                dayOfMonth = 1,
                time = LocalTime.now()
            )
        ),
        RecurringExpenseInfoItem(
            recurringExpense = RecurringExpense(
                id = 2,
                title = "Spotify Premium",
                description = "Shared with friends",
                amount = 209.00,
                currency = Currency.getInstance("THB"),
                dayOfMonth = 9,
                time = LocalTime.now()
            )
        ),
        RecurringExpenseInfoItem(
            recurringExpense = RecurringExpense(
                id = 3,
                title = "Discord Nitro",
                amount = 350.00,
                currency = Currency.getInstance("THB"),
                dayOfMonth = 15,
                time = LocalTime.now()
            )
        )
    )
    val totalAmount = mockItems.sumOf { it.recurringExpense.amount }
    val currency = mockItems.firstOrNull()?.recurringExpense?.currency
        ?: Currency.getInstance(Locale.getDefault())
    MindExpenseTheme {
        Surface {
            BaseMonthlyListPage(
                cardInfoItems = mockItems,
                totalAmount = totalAmount,
                currency = currency
            )
        }
    }
}
