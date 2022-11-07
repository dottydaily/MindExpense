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
import com.purkt.mindexpense.expense.domain.model.ExpenseListMode
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.ExpenseSummary
import com.purkt.model.domain.model.IndividualExpense
import com.purkt.model.domain.model.RecurringExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.DateTimeException
import java.time.LocalDate
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
    private val _expenseListModeState = mutableStateOf(ExpenseListMode.INDIVIDUAL)
    private val _expenseSummaryFlow = MutableStateFlow<ExpenseSummary?>(null)
    private val _recurringExpenseSummaryFlow = MutableStateFlow<ExpenseSummary?>(null)
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
     * A State of target expense data to show on this page.
     */
    val expenseListModeState: State<ExpenseListMode> = _expenseListModeState

    /**
     * A State of the [ExpenseSummary] that contains [IndividualExpense] or [RecurringExpense] detail to show in UI.
     */
    val expenseSummaryFlow: StateFlow<ExpenseSummary?> = _expenseSummaryFlow

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
     * The target expense data will depend on [expenseListModeState].
     * - If it is [ExpenseListMode.INDIVIDUAL], then load [IndividualExpense].
     * - If it is [ExpenseListMode.MONTHLY], then load [RecurringExpense].
     *
     * This method will observe database and emit the updated data to [expenseSummaryFlow].
     */
    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        _loadingState.value = true
        delay(150L)
        val targetFlow = when (_expenseListModeState.value) {
            ExpenseListMode.INDIVIDUAL -> {
                findAllIndividualExpensesUseCase.invoke()
                    .transform { individualExpenses ->
                        val summary = createIndividualExpenseSummary(individualExpenses)
                        emit(summary)
                    }
            }
            ExpenseListMode.MONTHLY -> {
                findAllRecurringExpensesUseCase.invoke()
                    .transform { recurringExpenses ->
                        val summary = createRecurringExpenseSummary(recurringExpenses)
                        emit(summary)
                    }
            }
        }
        targetFlow.collectExpenseSummaryFromTargetFlow(
            toDoAfterCollected = {
                if (_loadingState.value) _loadingState.value = false
            }
        )
    }

    /**
     * Delete the target expense from database and UI.
     *
     * This method will trigger update on [expenseSummaryFlow] and [deleteStatusState]
     *
     * @param expense The target [IndividualExpense] which it will be deleted.
     */
    fun deleteExpense(expense: IndividualExpense) = viewModelScope.launch(ioDispatcher) {
        val expensesByDate = _expenseSummaryFlow.value?.expensesByDate

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
                val newTotalAmount = _expenseSummaryFlow.value?.expensesByDate?.values
                    ?.sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
                    ?: 0.0
                _totalAmountState.value = newTotalAmount
                _totalCurrencyState.value = _expenseSummaryFlow.value?.expensesByDate?.values?.firstOrNull()
                    ?.expenses?.firstOrNull()
                    ?.currency?.currencyCode
                    ?: ""
                _expenseSummaryFlow.value = _expenseSummaryFlow.value?.copy()
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

    fun setListModeToCommon() {
        if (!_loadingState.value && _expenseListModeState.value != ExpenseListMode.INDIVIDUAL) {
            _expenseListModeState.value = ExpenseListMode.INDIVIDUAL
        }
        fetchAllExpenses()
    }

    fun setListModeToMonthly() {
        if (!_loadingState.value && _expenseListModeState.value != ExpenseListMode.MONTHLY) {
            _expenseListModeState.value = ExpenseListMode.MONTHLY
        }
        fetchAllExpenses()
    }

    fun fetchPreviousMonth() {
        if (!_loadingState.value) {
            startDate = startDate.minusMonths(1)
            endDate = endDate.minusMonths(1)
            fetchAllExpenses()
        }
    }

    fun fetchNextMonth() {
        if (!_loadingState.value) {
            startDate = startDate.plusMonths(1)
            endDate = endDate.plusMonths(1)
            fetchAllExpenses()
        }
    }

    private suspend fun createIndividualExpenseSummary(list: List<IndividualExpense>): ExpenseSummary {
        return withContext(ioDispatcher) {
            val expensesGroupByDate = list.sortedByDescending { it.dateTime }
                .groupBy { it.dateTime.toLocalDate() }
                .mapValues {
                    DailyExpenses(
                        it.value.toMutableList(),
                        it.key
                    )
                }

            val filteredExpenses = expensesGroupByDate
                .filterKeys { it.isLocalDateInRangeOf(startDate, endDate) }
                .toMutableMap()

            return@withContext ExpenseSummary(
                expensesByDate = filteredExpenses,
                startDate = startDate,
                endDate = endDate
            )
        }
    }

    private fun LocalDate.isLocalDateInRangeOf(startDate: LocalDate, endDate: LocalDate): Boolean {
        return isEqual(startDate) || (isAfter(startDate) && isBefore(endDate)) || isEqual(endDate)
    }

    private suspend fun createRecurringExpenseSummary(list: List<RecurringExpense>): ExpenseSummary {
        return withContext(ioDispatcher) {
            val expensesGroupByDate = mutableMapOf<LocalDate, DailyExpenses>()

            list.sortedByDescending { it.dayOfMonth }.forEachIndexed { index, recurringExpense ->
                val targetConvertedExpenseId = "${IndividualExpense.PREFIX_ID_FOR_RECURRING_EXPENSE}$index".toInt()

                val targetExpenseGroup = expensesGroupByDate.filterKeys { it.dayOfMonth == recurringExpense.dayOfMonth }
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
                        expensesGroupByDate[newDate] = newDailyExpense
                    }
                }
            }

            return@withContext ExpenseSummary(
                expensesByDate = expensesGroupByDate,
                startDate = startDate,
                endDate = endDate
            )
        }
    }

    private suspend fun Flow<ExpenseSummary>.collectExpenseSummaryFromTargetFlow(
        toDoAfterCollected: suspend () -> Unit = {}
    ) {
        collect { thisMonthExpenses ->
            setUiDataFromExpenseSummary(thisMonthExpenses)
            toDoAfterCollected.invoke()
        }
    }

    private fun setUiDataFromExpenseSummary(summary: ExpenseSummary) {
        _totalAmountState.value = summary.expensesByDate.values
            .sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
        _totalCurrencyState.value = summary.expensesByDate.values.firstOrNull()
            ?.expenses?.firstOrNull()
            ?.currency?.currencyCode
            ?: ""
        _expenseSummaryFlow.value = summary
    }
}
