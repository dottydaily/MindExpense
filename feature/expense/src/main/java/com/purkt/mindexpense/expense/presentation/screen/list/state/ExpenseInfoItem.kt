package com.purkt.mindexpense.expense.presentation.screen.list.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.purkt.database.domain.model.Expense
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class ExpenseInfoItem {
    class ExpenseCardDetail(val expense: Expense, isExpanded: Boolean = false) : ExpenseInfoItem() {
        var isExpanded by mutableStateOf(isExpanded)
    }

    class ExpenseGroupDate(val date: LocalDate) : ExpenseInfoItem() {
        val dateString: String = try {
            DateTimeFormatter.ofPattern("d MMM yyyy").format(date)
        } catch (e: Throwable) {
            Timber.e("Can't parse date string from $date")
            ""
        }
    }
}