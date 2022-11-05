package com.purkt.mindexpense.monthly.presentation.screen.additem

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.recurringexpense.AddRecurringExpenseUseCase
import com.purkt.database.domain.usecase.recurringexpense.FindRecurringExpenseByIdUseCase
import com.purkt.database.domain.usecase.recurringexpense.UpdateRecurringExpenseUseCase
import com.purkt.mindexpense.monthly.domain.model.RecurringExpenseForm
import com.purkt.mindexpense.monthly.presentation.screen.additem.state.AddRecurringExpenseStatus
import com.purkt.mindexpense.monthly.presentation.screen.additem.state.RecurringExpenseAddInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MonthlyExpenseAddViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val findRecurringExpenseByIdUseCase: FindRecurringExpenseByIdUseCase,
    private val addRecurringExpenseUseCase: AddRecurringExpenseUseCase,
    private val updateRecurringExpenseUseCase: UpdateRecurringExpenseUseCase
) : ViewModel() {
    var addInfo = mutableStateOf(RecurringExpenseAddInfoState())

    private val _addStatusState = mutableStateOf<AddRecurringExpenseStatus>(AddRecurringExpenseStatus.Idle)
    val addStatusState: State<AddRecurringExpenseStatus> = _addStatusState

    private var isUpdatedExpense: Boolean = false

    fun loadExpenseId(id: Int) = viewModelScope.launch(ioDispatcher) {
        val target = findRecurringExpenseByIdUseCase.invoke(id)
        target?.let { expense ->
            addInfo.value.let { state ->
                state.recurringExpenseId = id
                state.title = expense.title
                state.description = expense.description
                state.amount = DecimalFormat("###0.##").format(expense.amount)
                state.dayOfMonth = expense.dayOfMonth.toString()
            }

            // Set flag to update expense instead of creating the new one.
            isUpdatedExpense = true
        }
    }

    fun saveExpense(expenseInfo: RecurringExpenseAddInfoState) = viewModelScope.launch(ioDispatcher) {
        try {
            val amount = expenseInfo.amount.toDouble()
            val form = RecurringExpenseForm(
                id = expenseInfo.recurringExpenseId,
                title = expenseInfo.title,
                description = expenseInfo.description,
                amount = amount,
                currency = Currency.getInstance("THB"),
                dayOfMonth = expenseInfo.dayOfMonth.toIntOrNull() ?: 0
            ).apply {
                if (!isTitleValid()) {
                    expenseInfo.isTitleInvalid = true
                }
                if (!isAmountValid()) {
                    expenseInfo.isAmountInvalid = true
                }
                if (!isDayOfMonthValid()) {
                    expenseInfo.isDayOfMonthInvalid = true
                }
            }
            val targetExpense = form.createExpenseOrNull()
                ?: throw Exception("Invalid recurring expense data")

            if (isUpdatedExpense) {
                isUpdatedExpense = false
                val isUpdated = updateRecurringExpenseUseCase.invoke(targetExpense)

                if (!isUpdated) {
                    throw Exception("Can't update recurring expense info to database : $targetExpense")
                }
                _addStatusState.value = AddRecurringExpenseStatus.Success
            } else {
                val isAdded = addRecurringExpenseUseCase.invoke(targetExpense)

                if (!isAdded) {
                    throw Exception("Can't add recurring expense info to database")
                }
                _addStatusState.value = AddRecurringExpenseStatus.Success
            }
        } catch (e: Exception) {
            Timber.e(e.message)
            _addStatusState.value = AddRecurringExpenseStatus.Failed
        }
    }
}
