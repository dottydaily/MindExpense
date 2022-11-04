package com.purkt.mindexpense.monthly.presentation.screen.list.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.purkt.model.domain.model.RecurringExpense
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class MonthlyExpenseInfoItem {
    class ExpenseCardDetail(val recurringExpense: RecurringExpense, isExpanded: Boolean = false) {
        var isExpanded by mutableStateOf(isExpanded)
    }

    class ExpenseDateDetail(val date: LocalDate) : MonthlyExpenseInfoItem() {
        val dateString: String = try {
            DateTimeFormatter.ofPattern("eee, MMM d, yyyy").format(date)
        } catch (e: Throwable) {
            Timber.e("Can't parse date string from $date")
            ""
        }
    }
}
