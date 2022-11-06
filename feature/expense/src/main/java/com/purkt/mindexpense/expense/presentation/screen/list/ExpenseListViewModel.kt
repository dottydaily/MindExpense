package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.individualexpense.DeleteIndividualExpenseUseCase
import com.purkt.database.domain.usecase.individualexpense.FindAllIndividualExpensesUseCase
import com.purkt.database.domain.usecase.recurringexpense.FindAllRecurringExpensesUseCase
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.ExpenseSummary
import com.purkt.model.domain.model.IndividualExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val findAllIndividualExpensesUseCase: FindAllIndividualExpensesUseCase,
    private val findAllRecurringExpensesUseCase: FindAllRecurringExpensesUseCase,
    private val deleteIndividualExpenseUseCase: DeleteIndividualExpenseUseCase
) : ViewModel() {
    private val _loadingState = mutableStateOf(true)
    private val _expenseSummary = MutableStateFlow<ExpenseSummary?>(null)
    private val _deleteStatusState = mutableStateOf<DeleteExpenseStatus>(DeleteExpenseStatus.Idle)
    private val _totalAmountState = mutableStateOf(0.0)
    private val _totalCurrencyState = mutableStateOf("")

    // Date related
    private var startDayEachMonth = 25
    private var startDate: LocalDate = LocalDate.now().run {
        var targetDate = this
        if (dayOfMonth < startDayEachMonth) {
            targetDate = targetDate.minusMonths(1)
        }
        targetDate = targetDate.withDayOfMonth(startDayEachMonth)
        targetDate
    }
    private var endDate: LocalDate = startDate.plusMonths(1).minusDays(1)

    /**
     * A State of loading status of this page.
     */
    val loadingState: State<Boolean> = _loadingState

    /**
     * A State of the [ExpenseSummary] to show in UI.
     */
    val expenseSummaryFlow: StateFlow<ExpenseSummary?> = _expenseSummary

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
     * This method will observe database and emit the updated data to [expenseSummaryFlow].
     */
    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        _loadingState.value = true
        findAllIndividualExpensesUseCase.invoke()
            .transform { expensesFromDb ->
                val thisMonthExpenses = mapToExpenseSummary(expensesFromDb)
                emit(thisMonthExpenses)
            }
            .collect { thisMonthExpenses ->
                _totalAmountState.value = thisMonthExpenses.expensesByDate.values
                    .sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
                _totalCurrencyState.value = thisMonthExpenses.expensesByDate.values.firstOrNull()
                    ?.expenses?.firstOrNull()
                    ?.currency?.currencyCode
                    ?: ""
                _expenseSummary.value = thisMonthExpenses
                if (_loadingState.value) _loadingState.value = false
            }
    }

    /**
     * Delete the target expense from database and UI.
     *
     * This method will trigger update on [expenseSummaryFlow] and [deleteStatusState]
     *
     * @param expense The target [IndividualExpense] which it will be deleted.
     */
    fun deleteExpense(expense: IndividualExpense) = viewModelScope.launch(ioDispatcher) {
        val expensesByDate = _expenseSummary.value?.expensesByDate

        val targetDate = expense.dateTime.toLocalDate()
        val targetDailyExpenses = expensesByDate?.get(targetDate)
        val targetExpense = targetDailyExpenses?.expenses
            ?.find { it.id == expense.id }
        if (targetExpense == null) {
            _deleteStatusState.value = DeleteExpenseStatus.DataNotFoundInUi
        } else {
            val isDeleted = deleteIndividualExpenseUseCase.invoke(targetExpense)
            if (isDeleted) {
                // Remove expense from the target daily expenses
                targetDailyExpenses.expenses.removeIf { it.id == expense.id }

                // Check and remove if the current date has no data
                if (targetDailyExpenses.expenses.isEmpty()) {
                    expensesByDate.remove(targetDate)
                }

                // Update each information )
                val newTotalAmount = _expenseSummary.value?.expensesByDate?.values
                    ?.sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
                    ?: 0.0
                _totalAmountState.value = newTotalAmount
                _totalCurrencyState.value = _expenseSummary.value?.expensesByDate?.values?.firstOrNull()
                    ?.expenses?.firstOrNull()
                    ?.currency?.currencyCode
                    ?: ""
                _expenseSummary.value = _expenseSummary.value?.copy()
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

    fun fetchPreviousMonth() {
        startDate = startDate.minusMonths(1)
        endDate = endDate.minusMonths(1)
        fetchAllExpenses()
    }

    fun fetchNextMonth() {
        startDate = startDate.plusMonths(1)
        endDate = endDate.plusMonths(1)
        fetchAllExpenses()
    }

    private suspend fun mapToExpenseSummary(list: List<IndividualExpense>): ExpenseSummary = withContext(ioDispatcher) {
        val expensesGroupByDate = list.groupBy { it.dateTime.toLocalDate() }
            .mapValues {
                DailyExpenses(
                    it.value.toMutableList(),
                    it.key
                )
            }

        val filteredExpenses = expensesGroupByDate
            .filterKeys { it.isLocalDateInRangeOf(startDate, endDate) }.toMutableMap()

        // Add monthly expense to the summary
        val recurringExpenses = findAllRecurringExpensesUseCase.invoke()
            .catch {
                Timber.e("Error getting all recurring expenses")
                emit(emptyList())
            }
            .firstOrNull()

        recurringExpenses?.forEachIndexed { index, recurringExpense ->
            val targetConvertedExpenseId = "${IndividualExpense.PREFIX_ID_FOR_RECURRING_EXPENSE}$index".toInt()

            val targetExpenseGroup = filteredExpenses.filterKeys { it.dayOfMonth == recurringExpense.dayOfMonth }
            if (targetExpenseGroup.isNotEmpty()) {
                val dailyExpense = targetExpenseGroup.values.firstOrNull()
                dailyExpense?.run {
                    val convertedExpense = recurringExpense.mapToIndividualExpense(
                        id = targetConvertedExpenseId,
                        targetMonth = date.month,
                        targetYear = date.year
                    )
                    if (convertedExpense != null) {
                        expenses.add(0, convertedExpense)
                    }
                }
            } else {
                // create the new expense group by date
                val baseNewDate = if (recurringExpense.dayOfMonth < startDayEachMonth) {
                    startDate.plusMonths(1)
                } else {
                    startDate
                }
                val newDate = try {
                    baseNewDate.withDayOfMonth(recurringExpense.dayOfMonth)
                } catch (e: DateTimeException) {
                    Timber.e("Can't create new expense group by new date: ${e.message}")
                    // create new date with last day of month instead
                    baseNewDate.with(TemporalAdjusters.lastDayOfMonth())
                }

                val convertedExpense = recurringExpense.mapToIndividualExpense(
                    id = targetConvertedExpenseId,
                    targetMonth = newDate.month,
                    targetYear = newDate.year
                )
                if (convertedExpense != null) {
                    // create new daily expense
                    val newDailyExpense = DailyExpenses(
                        expenses = mutableListOf(convertedExpense),
                        date = newDate
                    )
                    // add to expense group
                    filteredExpenses[newDate] = newDailyExpense
                }
            }
        }

        val resultExpenses = filteredExpenses.toSortedMap(Comparator.reverseOrder())

        return@withContext ExpenseSummary(
            expensesByDate = resultExpenses,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun LocalDate.isLocalDateInRangeOf(startDate: LocalDate, endDate: LocalDate): Boolean {
        return isEqual(startDate) || (isAfter(startDate) && isBefore(endDate)) || isEqual(endDate)
    }
}
