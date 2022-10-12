package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.usecase.DeleteExpenseUseCase
import com.purkt.database.domain.usecase.FindAllExpensesUseCase
import com.purkt.mindexpense.expense.domain.model.DailyExpenses
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.domain.model.MonthlyExpenses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val findAllExpensesUseCase: FindAllExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {
    private val _loadingState = mutableStateOf(true)
    private val _monthlyExpenses = MutableStateFlow<MonthlyExpenses?>(null)
    private val _deleteStatusState = mutableStateOf<DeleteExpenseStatus>(DeleteExpenseStatus.Idle)
    private val _totalAmountState = mutableStateOf(0.0)
    private val _totalCurrencyState = mutableStateOf("")

    /**
     * A State of loading status of this page.
     */
    val loadingState: State<Boolean> = _loadingState

    /**
     * A State of the [MonthlyExpenses] to show in UI.
     */
    val monthlyExpensesFlow: StateFlow<MonthlyExpenses?> = _monthlyExpenses

    /**
     * A State of delete status.
     */
    val deleteStatusState: State<DeleteExpenseStatus> = _deleteStatusState

    /**
     * A State of the total amount of all expenses.
     */
    val totalAmountState: State<Double> = _totalAmountState

    /**
     * A State of the total amount's currency string.
     */
    val totalCurrencyStringState: State<String> = _totalCurrencyState

    /**
     * Start fetching all expense data from database.
     *
     * This method will observe database and emit the updated data to [monthlyExpensesFlow].
     */
    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        findAllExpensesUseCase.invoke()
            .transform { expenseListFromDb ->
                val thisMonthExpenses = mapToMonthlyExpenses(expenseListFromDb)
                emit(thisMonthExpenses)
            }
            .collect { thisMonthExpenses ->
                _totalAmountState.value = thisMonthExpenses.expensesByDate.values
                    .sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
                _totalCurrencyState.value = thisMonthExpenses.expensesByDate.values.firstOrNull()
                    ?.expenses?.firstOrNull()
                    ?.currency?.currencyCode
                    ?: ""
                _monthlyExpenses.value = thisMonthExpenses
                if (_loadingState.value) _loadingState.value = false
            }
    }

    /**
     * Delete the target expense from database and UI.
     *
     * This method will trigger update on [monthlyExpensesFlow] and [deleteStatusState]
     *
     * @param expense The target [Expense] which it will be deleted.
     */
    fun deleteExpense(expense: Expense) = viewModelScope.launch(ioDispatcher) {
        val expensesByDate = _monthlyExpenses.value?.expensesByDate

        val targetDate = expense.dateTime.toLocalDate()
        val targetDailyExpenses = expensesByDate?.get(targetDate)
        val targetExpense = targetDailyExpenses?.expenses
            ?.find { it.id == expense.id }
        if (targetExpense == null) {
            _deleteStatusState.value = DeleteExpenseStatus.DataNotFoundInUi
        } else {
            val isDeleted = deleteExpenseUseCase.invoke(targetExpense)
            if (isDeleted) {
                // Remove expense from the target daily expenses
                targetDailyExpenses.expenses.removeIf { it.id == expense.id }

                // Check and remove if the current date has no data
                if (targetDailyExpenses.expenses.isEmpty()) {
                    expensesByDate.remove(targetDate)
                }

                // Update each information )
                val newTotalAmount = _monthlyExpenses.value?.expensesByDate?.values
                    ?.sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
                    ?: 0.0
                _totalAmountState.value = newTotalAmount
                _totalCurrencyState.value = _monthlyExpenses.value?.expensesByDate?.values?.firstOrNull()
                    ?.expenses?.firstOrNull()
                    ?.currency?.currencyCode
                    ?: ""
                _monthlyExpenses.value = _monthlyExpenses.value?.copy()
                _deleteStatusState.value = DeleteExpenseStatus.Success
            } else {
                _deleteStatusState.value = DeleteExpenseStatus.Failed
            }
        }
    }

    /**
     * Reset [deleteStatusState] to be [DeleteExpenseStatus.Idle]
     */
    fun resetDeleteStatusToIdle() {
        _deleteStatusState.value = DeleteExpenseStatus.Idle
    }

    private fun mapToMonthlyExpenses(list: List<Expense>): MonthlyExpenses {
        val now = LocalDate.now()
        val startDayOfEachMonth = 25
        val isPassStartDate = now.dayOfMonth >= startDayOfEachMonth
        val startDate = LocalDate.now().withDayOfMonth(startDayOfEachMonth).run {
            if (!isPassStartDate) {
                withMonth(monthValue - 1)
            } else this
        }
        val endDate = startDate.plusMonths(1).minusDays(1)
        val expensesGroupByDate = list.sortedByDescending { it.dateTime }.groupBy { it.dateTime.toLocalDate() }
            .mapValues { DailyExpenses(it.value.toMutableList(), it.key) }

        val filteredExpenses = expensesGroupByDate.filterKeys {
            it.isEqual(startDate) || (it.isAfter(startDate) && it.isBefore(endDate)) || it.isEqual(endDate)
        }.toMutableMap()

        return MonthlyExpenses(expensesByDate = filteredExpenses, startDate = startDate, endDate = endDate)
    }
}
