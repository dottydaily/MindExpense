package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.DeleteExpenseUseCase
import com.purkt.database.domain.usecase.FindAllExpensesUseCase
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.ExpenseScreen
import com.purkt.mindexpense.expense.presentation.screen.list.state.DeleteExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseCardInfoState
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
    private val _deleteStatusState = mutableStateOf<DeleteExpenseStatus>(DeleteExpenseStatus.Idle)
    private val _cardInfoStateFlow = MutableStateFlow<List<ExpenseCardInfoState>>(emptyList())

    /**
     * A State of loading status of this page.
     */
    val loadingState: State<Boolean> = _loadingStateFlow

    /**
     * A State of delete status.
     */
    val deleteStatusState: State<DeleteExpenseStatus> = _deleteStatusState

    /**
     * A State of the list of [ExpenseCardInfoState] to be used to show in UI.
     */
    val cardInfoStateFlow: StateFlow<List<ExpenseCardInfoState>> = _cardInfoStateFlow

    /**
     * Start fetching all expense data from database.
     *
     * This method will observe database and trigger update on [cardInfoStateFlow] every time the expense table is changed.
     */
    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        findAllExpensesUseCase.invoke()
            .transform { expenseList ->
                val cardInfoStateList = expenseList.map {
                    ExpenseCardInfoState(it)
                }.sortedByDescending { it.expense.dateTime }
                emit(cardInfoStateList)
            }
            .flowOn(ioDispatcher)
            .collect {
                _cardInfoStateFlow.value = it
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
     * Set flag to expand or collapse the card info depends on its current expanded status.
     *
     * This method will trigger update on [cardInfoStateFlow].
     *
     * @param state The target state which it need to set flag to show/hide card info.
     */
    fun changeCardInfoExpandedState(state: ExpenseCardInfoState) {
        val newList = _cardInfoStateFlow.value.map {
            if (it.expense.id == state.expense.id) {
                it.isExpanded = !it.isExpanded
            }
            it
        }

        _cardInfoStateFlow.value = newList
    }

    /**
     * Delete the target expense from database and UI.
     *
     * This method will trigger update on [cardInfoStateFlow] and [deleteStatusState]
     *
     * @param state The target state which it has a target expense to be deleted.
     */
    fun deleteExpense(state: ExpenseCardInfoState) = viewModelScope.launch(ioDispatcher) {
        val targetExpenseInfo = _cardInfoStateFlow.value.find { it.expense == state.expense }
        if (targetExpenseInfo == null) {
            _deleteStatusState.value = DeleteExpenseStatus.DataNotFoundInUi
        } else {
            val isDeleted = deleteExpenseUseCase.invoke(targetExpenseInfo.expense)
            if (isDeleted) {
                val newList = _cardInfoStateFlow.value.toMutableList().apply {
                    removeIf { it.expense == state.expense }
                }
                _cardInfoStateFlow.value = newList
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
}
