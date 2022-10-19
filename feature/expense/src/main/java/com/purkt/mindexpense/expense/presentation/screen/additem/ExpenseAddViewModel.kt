package com.purkt.mindexpense.expense.presentation.screen.additem

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.usecase.AddExpenseUseCase
import com.purkt.database.domain.usecase.FindExpenseByIdUseCase
import com.purkt.database.domain.usecase.UpdateExpenseUseCase
import com.purkt.mindexpense.expense.domain.model.ExpenseForm
import com.purkt.mindexpense.expense.presentation.screen.additem.state.AddExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.additem.state.ExpenseAddInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExpenseAddViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val findExpenseByIdUseCase: FindExpenseByIdUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase
) : ViewModel() {
    var addInfo = mutableStateOf(ExpenseAddInfoState())

    private val _addStatusState = mutableStateOf<AddExpenseStatus>(AddExpenseStatus.Idle)
    val addStatusState: State<AddExpenseStatus> = _addStatusState

    private var isUpdatedExpense: Boolean = false

    fun loadExpenseId(id: Int) = viewModelScope.launch(ioDispatcher) {
        val target = findExpenseByIdUseCase.invoke(id)
        target?.let { expense ->
            addInfo.value.let { state ->
                state.expenseId = id
                state.title = expense.title
                state.description = expense.description
                state.amount = DecimalFormat("###0.##").format(expense.amount)
                with(expense.dateTime) {
                    state.date = getDateString(dayOfMonth, month.value - 1, year) ?: ""
                    state.time = getTimeString(hour, minute) ?: ""
                }
            }

            // Set flag to update expense instead of creating the new one.
            isUpdatedExpense = true
        }
    }

    fun saveExpense(expenseInfo: ExpenseAddInfoState) = viewModelScope.launch(ioDispatcher) {
        try {
            val amount = expenseInfo.amount.toDouble()
            val form = ExpenseForm(
                id = expenseInfo.expenseId,
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

            if (isUpdatedExpense) {
                isUpdatedExpense = false
                val isUpdated = updateExpenseUseCase.invoke(targetExpense)

                if (!isUpdated) {
                    throw Exception("Can't update expense info to database : $targetExpense")
                }
                _addStatusState.value = AddExpenseStatus.Success
            } else {
                val isAdded = addExpenseUseCase.invoke(targetExpense)

                if (!isAdded) {
                    throw Exception("Can't add expense info to database")
                }
                _addStatusState.value = AddExpenseStatus.Success
            }
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
