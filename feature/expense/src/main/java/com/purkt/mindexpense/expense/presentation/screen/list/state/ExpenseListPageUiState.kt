package com.purkt.mindexpense.expense.presentation.screen.list.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.domain.model.ExpenseListMode
import com.purkt.mindexpense.expense.domain.model.ExpenseListResult
import java.time.LocalDate

class ExpenseListPageUiState {
    /**
     * A [MutableState] of initialize status on this page.
     */
    val isInitializedState = mutableStateOf(false)

    /**
     * A [MutableState] of start date to show on this page.
     */
    val startDateState = mutableStateOf<LocalDate>(LocalDate.now())

    /**
     * A [MutableState] of end date data to show on this page.
     */
    val endDateState = mutableStateOf<LocalDate>(
        startDateState.value.plusMonths(1).minusDays(1)
    )

    /**
     * A [MutableState] of target expense data to show on this page.
     */
    val expenseListModeState = mutableStateOf(ExpenseListMode.INDIVIDUAL)

    /**
     * A [MutableState] of the [ExpenseListResult] detail to show in UI.
     */
    val expenseListResultState = mutableStateOf<ExpenseListResult>(ExpenseListResult.Loading)

    /**
     * A [MutableState] of delete status.
     */
    val deleteStatusState = mutableStateOf<DeleteExpenseStatus>(DeleteExpenseStatus.Idle)

    /**
     * A [MutableState] of the total amount of all expenses.
     */
    val totalAmountState = mutableStateOf(0.0)

    /**
     * A [MutableState] of the total amount's currency string.
     */
    val totalCurrencyState = mutableStateOf("")
}
