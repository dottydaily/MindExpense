package com.purkt.mindexpense.expense.presentation.screen.list.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.purkt.database.domain.model.Expense

class ExpenseCardInfoState(
    val expense: Expense,
    isExpanded: Boolean = false
) {
    var isExpanded by mutableStateOf(isExpanded)
}
