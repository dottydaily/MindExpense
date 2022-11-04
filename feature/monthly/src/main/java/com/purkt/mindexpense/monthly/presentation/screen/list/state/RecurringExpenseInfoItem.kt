package com.purkt.mindexpense.monthly.presentation.screen.list.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.purkt.model.domain.model.RecurringExpense

class RecurringExpenseInfoItem(val recurringExpense: RecurringExpense, isExpanded: Boolean = false) {
    var isExpanded by mutableStateOf(isExpanded)
}
