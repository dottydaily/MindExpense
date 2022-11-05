package com.purkt.mindexpense.monthly.presentation.screen.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.recurringexpense.DeleteRecurringExpenseUseCase
import com.purkt.database.domain.usecase.recurringexpense.FindAllRecurringExpensesUseCase
import com.purkt.mindexpense.monthly.domain.model.DeleteRecurringExpenseStatus
import com.purkt.model.domain.model.RecurringExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthlyExpenseListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val findAllRecurringExpensesUseCase: FindAllRecurringExpensesUseCase,
    private val deleteRecurringExpenseUseCase: DeleteRecurringExpenseUseCase
) : ViewModel() {
    private val _loadingState = mutableStateOf(true)
    private var _recurringExpenses = MutableStateFlow<List<RecurringExpense>>(emptyList())
    private val _deleteStatusState = mutableStateOf<DeleteRecurringExpenseStatus>(DeleteRecurringExpenseStatus.Idle)
    private val _totalAmountState = mutableStateOf(0.0)
    private val _totalCurrencyState = mutableStateOf("")

    /**
     * A State of loading status of this page.
     */
    val loadingState: State<Boolean> = _loadingState

    /**
     * A State of the list of [RecurringExpense] to show in UI.
     */
    val recurringExpenses: StateFlow<List<RecurringExpense>> = _recurringExpenses

    /**
     * A State of delete status.
     */
    val deleteStatusState: State<DeleteRecurringExpenseStatus> = _deleteStatusState

    /**
     * A State of the total amount of all expenses.
     */
    val totalAmountState: State<Double> = _totalAmountState

    /**
     * A State of the total amount's currency string.
     */
    val totalCurrencyStringState: State<String> = _totalCurrencyState

    /**
     * Start fetching all recurring expense data from database.
     *
     * This method will observe database and emit the updated data to [recurringExpenses].
     */
    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        _loadingState.value = true
        findAllRecurringExpensesUseCase.invoke()
            .collect { recurringExpenses ->
                _totalAmountState.value = recurringExpenses.sumOf { it.amount }
                _totalCurrencyState.value = recurringExpenses.firstOrNull()?.currency?.currencyCode ?: ""
                _recurringExpenses.value = recurringExpenses.sortedBy { it.dayOfMonth }
                if (_loadingState.value) _loadingState.value = false
            }
    }

    /**
     * Delete the target recurring expense from database and UI.
     *
     * This method will trigger update on [recurringExpenses] and [deleteStatusState]
     *
     * @param expense The target [RecurringExpense] which it will be deleted.
     */
    fun deleteExpense(expense: RecurringExpense) = viewModelScope.launch(ioDispatcher) {
        val targetExpense = _recurringExpenses.value.find { it.id == expense.id }
        if (targetExpense == null) {
            _deleteStatusState.value = DeleteRecurringExpenseStatus.DataNotFoundInUi
        } else {
            val isDeleted = deleteRecurringExpenseUseCase.invoke(targetExpense)
            if (isDeleted) {
                // Remove expense from the target daily expenses
                val newList = _recurringExpenses.value.toMutableList().apply {
                    removeIf { it.id == expense.id }
                }
                _recurringExpenses.value = newList

                // Update each information
                val newTotalAmount = _recurringExpenses.value.sumOf { it.amount }
                _totalAmountState.value = newTotalAmount
                _totalCurrencyState.value = _recurringExpenses.value.firstOrNull()?.currency?.currencyCode ?: ""
                _deleteStatusState.value = DeleteRecurringExpenseStatus.Success
            } else {
                _deleteStatusState.value = DeleteRecurringExpenseStatus.Failed
            }
        }
    }

    /**
     * Reset [deleteStatusState] to be [DeleteRecurringExpenseStatus.Idle]
     */
    fun resetDeleteStatusToIdle() {
        _deleteStatusState.value = DeleteRecurringExpenseStatus.Idle
    }
}
