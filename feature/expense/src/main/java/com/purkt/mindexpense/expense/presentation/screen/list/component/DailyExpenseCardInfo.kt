package com.purkt.mindexpense.expense.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.database.domain.model.Expense
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Composable
fun DailyExpenseCardInfo(
    modifier: Modifier = Modifier,
    date: LocalDate,
    expenses: List<Expense>
) {
    LazyColumn(
        modifier = Modifier
            .then(modifier),
        userScrollEnabled = false
    ) {
        item {
            DateLabel(
                modifier = Modifier
                    .padding(24.dp),
                dateDetail = ExpenseInfoItem.ExpenseDateDetail(date)
            )
        }
        items(items = expenses, key = { it.id }) {
            ExpenseCardInfo(
                cardDetail = ExpenseInfoItem.ExpenseCardDetail(it)
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDailyExpenseCardInfo() {
    val mockExpenses = listOf(
        Expense(
            id = 1,
            title = "Title 1",
            description = "Description 1",
            amount = 101.2,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        Expense(
            id = 2,
            title = "Title 2",
            description = "Description 2",
            amount = 201.2,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        ),
        Expense(
            id = 3,
            title = "Title 3",
            description = "Description 3",
            amount = 301.2,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now()
        )
    )
    MindExpenseTheme {
        Surface {
            DailyExpenseCardInfo(date = LocalDate.now(), expenses = mockExpenses)
        }
    }
}
