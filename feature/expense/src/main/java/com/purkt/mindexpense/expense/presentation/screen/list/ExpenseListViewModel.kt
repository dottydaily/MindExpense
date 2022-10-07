package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.usecase.DeleteExpenseUseCase
import com.purkt.database.domain.usecase.FindAllExpensesUseCase
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.ExpenseScreen
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val findAllExpensesUseCase: FindAllExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {
    private val _loadingStateFlow = mutableStateOf(true)
    private val _cardItemsStateFlow = MutableStateFlow<List<ExpenseInfoItem>>(emptyList())
    private val _deleteStatusState = mutableStateOf<DeleteExpenseStatus>(DeleteExpenseStatus.Idle)
    private val _totalAmountState = mutableStateOf(0.0)
    private val _totalCurrencyState = mutableStateOf("")

    /**
     * A State of loading status of this page.
     */
    val loadingState: State<Boolean> = _loadingStateFlow

    /**
     * A State of the list of [ExpenseInfoItem] to show in UI.
     */
    val cardInfoStateFlow: StateFlow<List<ExpenseInfoItem>> = _cardItemsStateFlow

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
     * This method will observe database and trigger update on [cardInfoStateFlow] every time the expense table is changed.
     */
    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        findAllExpensesUseCase.invoke()
            .transform { expenseListFromDb ->
                val expenseGroupedByDate = mapToExpenseItemInfo(expenseListFromDb)
                emit(expenseGroupedByDate)
            }
            .flowOn(ioDispatcher)
            .collect {
                val cardDetails = it.filterIsInstance<ExpenseInfoItem.ExpenseCardDetail>()
                _totalAmountState.value = cardDetails.sumOf { detail -> detail.expense.amount }
                _totalCurrencyState.value = cardDetails.firstOrNull()?.expense?.currency?.currencyCode ?: ""
                _cardItemsStateFlow.value = it
                if (_loadingStateFlow.value) _loadingStateFlow.value = false
            }
    }

    /**
     * Navigate to add expense screen.
     * @param navigator The navigator to do navigate process.
     */
    fun goToAddExpensePage(navigator: ExpenseNavigator) {
        navigator.navigateTo(ExpenseScreen.AddScreen)
    }

    /**
     * Delete the target expense from database and UI.
     *
     * This method will trigger update on [cardInfoStateFlow] and [deleteStatusState]
     *
     * @param item The target item which it has a target expense to be deleted.
     */
    fun deleteExpense(item: ExpenseInfoItem.ExpenseCardDetail) = viewModelScope.launch(ioDispatcher) {
        val targetExpenseInfo = _cardItemsStateFlow.value
            .filterIsInstance<ExpenseInfoItem.ExpenseCardDetail>()
            .find { it.expense.id == item.expense.id }
        if (targetExpenseInfo == null) {
            _deleteStatusState.value = DeleteExpenseStatus.DataNotFoundInUi
        } else {
            val isDeleted = deleteExpenseUseCase.invoke(targetExpenseInfo.expense)
            if (isDeleted) {
                val newList = _cardItemsStateFlow.value.toMutableList().apply {
                    removeIf { it is ExpenseInfoItem.ExpenseCardDetail && it.expense.id == item.expense.id }
                }
                _cardItemsStateFlow.value = newList
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

    private fun mapToExpenseItemInfo(list: List<Expense>): List<ExpenseInfoItem> {
        val expenseGroupedByDate = list.sortedByDescending { it.dateTime }.groupBy { it.dateTime.toLocalDate() }
        val targetList = mutableListOf<ExpenseInfoItem>()
        expenseGroupedByDate.forEach { (localDate, expenses) ->
            targetList.add(ExpenseInfoItem.ExpenseGroupDate(date = localDate))
            targetList.addAll(
                expenses.map { ex -> ExpenseInfoItem.ExpenseCardDetail(expense = ex) }
            )
        }

        return targetList
    }
}
