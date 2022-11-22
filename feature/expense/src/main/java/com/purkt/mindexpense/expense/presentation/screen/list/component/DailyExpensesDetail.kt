package com.purkt.mindexpense.expense.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.IndividualExpense
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Composable
fun DailyExpensesDetail(
    dailyExpenses: DailyExpenses,
    modifier: Modifier = Modifier,
    onEditExpense: (expenseId: Int) -> Unit = {},
    onDeleteExpense: (expense: IndividualExpense) -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(24.dp)
        ) {
            DailyDetailTitle(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                expenses = dailyExpenses.expenses,
                date = dailyExpenses.date
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            dailyExpenses.expenses.forEach { expense ->
                ExpenseCardInfo(
                    cardDetail = ExpenseInfoItem.ExpenseCardDetail(expense),
                    onEditExpense = onEditExpense,
                    onDeleteCard = onDeleteExpense
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDailyExpensesDetail() {
    MindExpenseTheme {
        Surface {
            DailyExpensesDetail(
                dailyExpenses = DailyExpenses(
                    expenses = mutableListOf(
                        IndividualExpense(
                            id = -11,
                            title = "Breakfast",
                            description = "Eat breakfast with friend at the mall",
                            amount = 699.00,
                            currency = Currency.getInstance("THB"),
                            dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
                        ),
                        IndividualExpense(
                            id = -12,
                            title = "Lunch",
                            description = "Eat lunch with friend at the mall",
                            amount = 699.00,
                            currency = Currency.getInstance("THB"),
                            dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
                        )
                    ),
                    date = LocalDate.of(2022, 7, 26)
                )
            )
        }
    }
}
