package com.purkt.mindexpense.expense.presentation.screen.list.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.purkt.model.domain.model.IndividualExpense
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class ExpenseInfoItem {
    class ExpenseCardDetail(val expense: IndividualExpense, isExpanded: Boolean = false) : ExpenseInfoItem() {
        var isExpanded by mutableStateOf(isExpanded)
    }
}
