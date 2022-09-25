package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.FindAllExpensesUseCase
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.ExpenseScreen
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
    private val findAllExpensesUseCase: FindAllExpensesUseCase
) : ViewModel() {
    private val _loadingStateFlow = mutableStateOf(true)
    val loadingState: State<Boolean> = _loadingStateFlow

    private val _cardInfoStateFlow = MutableStateFlow<List<ExpenseCardInfoState>>(emptyList())
    val cardInfoStateFlow: StateFlow<List<ExpenseCardInfoState>> = _cardInfoStateFlow

    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        findAllExpensesUseCase.invoke()
            .transform { expenseList ->
                val cardInfoStateList = expenseList.map {
                    ExpenseCardInfoState(it)
                }.sortedBy { it.expense.dateTime }.reversed()
                emit(cardInfoStateList)
            }
            .flowOn(ioDispatcher)
            .collect {
                _cardInfoStateFlow.value = it
                if (_loadingStateFlow.value) _loadingStateFlow.value = false
            }
    }

    fun goToAddExpensePage(navigator: ExpenseNavigator) {
        navigator.navigateTo(ExpenseScreen.AddScreen)
    }

    fun changeCardInfoExpandedState(state: ExpenseCardInfoState) {
        val newList = _cardInfoStateFlow.value.map {
            if (it.expense.id == state.expense.id) {
                it.isExpanded = !it.isExpanded
            }
            it
        }

        _cardInfoStateFlow.value = newList
    }
}
