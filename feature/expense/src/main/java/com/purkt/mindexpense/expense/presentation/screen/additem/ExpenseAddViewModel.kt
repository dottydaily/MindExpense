package com.purkt.mindexpense.expense.presentation.screen.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.usecase.AddExpenseUseCase
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.ExpenseScreen
import com.purkt.mindexpense.expense.presentation.screen.additem.state.AddExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.additem.state.ExpenseAddInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseAddViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {
    private val _addResultStateFlow = MutableStateFlow<AddExpenseStatus>(AddExpenseStatus.Start)
    val addResultStateFlow: StateFlow<AddExpenseStatus> = _addResultStateFlow

    fun addExpense(expenseInfo: ExpenseAddInfoState) = viewModelScope.launch(ioDispatcher) {
        try {
            val amount = expenseInfo.amount.toDouble()
            val dateTime = expenseInfo.getLocalDateTime()
            val targetExpense = Expense(
                id = 0,
                title = expenseInfo.title,
                description = expenseInfo.description,
                amount = amount,
                currency = Currency.getInstance("THB"),
                dateTime = dateTime
            )
            val isAdded = addExpenseUseCase.invoke(targetExpense)

            if (!isAdded) {
                throw Exception("Can't add expense info to database")
            }
            _addResultStateFlow.value = AddExpenseStatus.Success
        } catch (e: Exception) {
            Timber.e(e.message)
            _addResultStateFlow.value = AddExpenseStatus.Failed
        }
    }

    fun goBackToPreviousPage(navigator: ExpenseNavigator) {
        navigator.popTo(ExpenseScreen.ListScreen)
    }

    fun getDateString(dayOfMonth: Int, monthValue: Int, year: Int): String? {
        return try {
            val date = LocalDate.of(year, monthValue, dayOfMonth)
            val formatter = DateTimeFormatter.ofPattern(ExpenseAddInfoState.DATE_PATTERN)
            formatter.format(date)
        } catch (e: Throwable) {
            Timber.e(e.message)
            null
        }
    }

    fun getTimeString(hourOfDay: Int, minute: Int): String? {
        return try {
            val time = LocalTime.of(hourOfDay, minute)
            val formatter = DateTimeFormatter.ofPattern(ExpenseAddInfoState.TIME_PATTERN)
            formatter.format(time)
        } catch (e: Throwable) {
            Timber.e(e.message)
            null
        }
    }
}
