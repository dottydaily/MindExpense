package com.purkt.mindexpense.expense.presentation.screen.list.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.domain.model.ExpenseListMode
import com.purkt.mindexpense.expense.domain.model.ExpenseListResult
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.IndividualExpense
import java.time.LocalDate

class ExpenseListPageUiState {
    private val _dailyExpensesStateList = mutableStateListOf<DailyExpenses>()

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
     * A [MutableState] of the [ExpenseListResult] status to show in UI.
     */
    val expenseListResultState = mutableStateOf(ExpenseListResult.LOADING)

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

    /**
     * A [List] of the [DailyExpenses] that observe from [SnapshotStateList] to show in UI.
     */
    val dailyExpensesStateList: List<DailyExpenses> = _dailyExpensesStateList

    /**
     * Replace the [newList] with the current [dailyExpensesStateList]
     * @param newList The target new list of [DailyExpenses] to be replaced with the current state list.
     */
    fun setNewList(newList: List<DailyExpenses>) {
        _dailyExpensesStateList.run {
            clear()
            addAll(newList)
        }
    }

    /**
     * Remove the [expense] from the [dailyExpensesStateList]
     * @param expense The target [IndividualExpense] to be deleted from the state list.
     */
    fun remove(expense: IndividualExpense) {
        _dailyExpensesStateList.apply {
            val targetDailyExpenses = find { it.date == expense.dateTime.toLocalDate() } ?: return
            val newExpenseList = targetDailyExpenses.expenses.toMutableList().apply {
                remove(expense)
            }
            val newDailyExpenses = targetDailyExpenses.copy(expenses = newExpenseList)

            val targetIndex = indexOf(targetDailyExpenses)
            if (targetIndex != -1) {
                remove(targetDailyExpenses)

                if (newDailyExpenses.expenses.isNotEmpty()) {
                    add(targetIndex, newDailyExpenses)
                }
            }
        }
    }
}
