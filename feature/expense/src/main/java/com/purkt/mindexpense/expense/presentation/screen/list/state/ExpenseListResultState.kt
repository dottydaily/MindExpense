package com.purkt.mindexpense.expense.presentation.screen.list.state

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.purkt.model.domain.model.DailyExpenses

sealed class ExpenseListResultState {
    object Loading : ExpenseListResultState()
    class ResultWithData(val expensesByDate: SnapshotStateList<DailyExpenses>) : ExpenseListResultState()
    object EmptyResult : ExpenseListResultState()
}
