package com.purkt.mindexpense.expense.presentation.screen.additem

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.AddExpenseUseCase
import com.purkt.mindexpense.expense.domain.model.ExpenseForm
import com.purkt.mindexpense.expense.presentation.screen.additem.state.AddExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.additem.state.ExpenseAddInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseAddViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {
    private val _addStatusState = mutableStateOf<AddExpenseStatus>(AddExpenseStatus.Idle)
    val addStatusState: State<AddExpenseStatus> = _addStatusState

    fun saveExpense(expenseInfo: ExpenseAddInfoState) = viewModelScope.launch(ioDispatcher) {
        try {
            val amount = expenseInfo.amount.toDouble()
            val form = ExpenseForm(
                id = 0,
                title = expenseInfo.title,
                description = expenseInfo.description,
                amount = amount,
                currency = Currency.getInstance("THB"),
                date = expenseInfo.getLocalDateTime().toLocalDate(),
                time = expenseInfo.getLocalDateTime().toLocalTime()
            ).apply {
                if (!isTitleValid()) {
                    expenseInfo.isTitleInvalid.value = true
                }
                if (!isAmountValid()) {
                    expenseInfo.isAmountInvalid.value = true
                }
            }
            val targetExpense = form.createExpenseOrNull()
                ?: throw Exception("Invalid expense data")
            val isAdded = addExpenseUseCase.invoke(targetExpense)

            if (!isAdded) {
                throw Exception("Can't add expense info to database")
            }
            _addStatusState.value = AddExpenseStatus.Success
        } catch (e: Exception) {
            Timber.e(e.message)
            _addStatusState.value = AddExpenseStatus.Failed
        }
    }

    fun getDateString(dayOfMonth: Int, monthValueCalendar: Int, year: Int): String? {
        return try {
            val date = LocalDate.of(year, Month.of(monthValueCalendar + 1), dayOfMonth)
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
