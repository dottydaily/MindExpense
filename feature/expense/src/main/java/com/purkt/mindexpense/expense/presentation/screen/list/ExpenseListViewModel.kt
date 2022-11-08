package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.individualexpense.DeleteIndividualExpenseUseCase
import com.purkt.database.domain.usecase.individualexpense.FindAllIndividualExpensesUseCase
import com.purkt.database.domain.usecase.recurringexpense.FindAllRecurringExpensesUseCase
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.domain.model.ExpenseListMode
import com.purkt.mindexpense.expense.domain.model.ExpenseListResult
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseListPageUiState
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.ExpenseSummary
import com.purkt.model.domain.model.IndividualExpense
import com.purkt.model.domain.model.RecurringExpense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    private var isLoading = true

    // Date related
    private var startDayEachMonth = 25

    val uiState = ExpenseListPageUiState()

    init {
        uiState.apply {
            val startDate = LocalDate.now().run {
                var targetDate = this
                if (dayOfMonth < startDayEachMonth) {
                    targetDate = targetDate.minusMonths(1)
                }
                targetDate = targetDate.withDayOfMonth(startDayEachMonth)
                targetDate
            }
            val endDate = startDate.plusMonths(1).minusDays(1)
            startDateState.value = startDate
            endDateState.value = endDate
            isInitializedState.value = true
        }
    }

    /**
     * Start fetching all expense data from database.
     *
     * The target expense data will depend on [ExpenseListPageUiState.expenseListModeState].
     * - If it is [ExpenseListMode.INDIVIDUAL], then load [IndividualExpense].
     * - If it is [ExpenseListMode.MONTHLY], then load [RecurringExpense].
     *
     * This method will observe database and emit the updated data
     * to [ExpenseListPageUiState.expenseListResultState].
     */
    fun fetchAllExpenses() = viewModelScope.launch(ioDispatcher) {
        isLoading = true
        val targetFlow = when (uiState.expenseListModeState.value) {
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
                isLoading = false
            }
        )
    }

    /**
     * Delete the target expense from database and UI.
     *
     * This method will trigger update on [ExpenseListPageUiState.expenseListResultState]
     * and [ExpenseListPageUiState.deleteStatusState]
     *
     * @param expense The target [IndividualExpense] which it will be deleted.
     */
    fun deleteExpense(expense: IndividualExpense) = viewModelScope.launch(ioDispatcher) {
        val expenseListResult = uiState.expenseListResultState.value
        if (expenseListResult is ExpenseListResult.ResultWithData) {
            val summary = expenseListResult.summary
            val expensesByDate = summary.expensesByDate

            val targetDate = expense.dateTime.toLocalDate()
            val targetDailyExpenses = expensesByDate[targetDate]
            val targetExpense = targetDailyExpenses?.expenses
                ?.find { it.id == expense.id }
            if (targetExpense == null) {
                uiState.deleteStatusState.value = DeleteExpenseStatus.DataNotFoundInUi
            } else {
                val isDeleted = deleteIndividualExpenseUseCase.invoke(targetExpense)
                if (isDeleted) {
                    // Remove expense from the target daily expenses
                    targetDailyExpenses.expenses.removeIf { it.id == expense.id }

                    // Check and remove if the current date has no data
                    if (targetDailyExpenses.expenses.isEmpty()) {
                        expensesByDate.remove(targetDate)
                    }

                    // Update each information
                    val newTotalAmount = expensesByDate.values
                        .sumOf { dailyExpense -> dailyExpense.getTotalAmount() }

                    uiState.apply {
                        totalAmountState.value = newTotalAmount
                        totalCurrencyState.value = expensesByDate.values.firstOrNull()
                            ?.expenses?.firstOrNull()
                            ?.currency?.currencyCode
                            ?: ""
                        expenseListResultState.value = ExpenseListResult.ResultWithData(summary)
                        deleteStatusState.value = DeleteExpenseStatus.Success
                    }
                } else {
                    uiState.deleteStatusState.value = DeleteExpenseStatus.Failed
                }
            }
        }
    }

    /**
     * Reset [ExpenseListPageUiState.deleteStatusState] to be [DeleteExpenseStatus.Idle]
     */
    fun resetDeleteStatusToIdle() {
        uiState.deleteStatusState.value = DeleteExpenseStatus.Idle
    }

    fun setListModeToCommon() {
        with(uiState) {
            if (!isLoading && expenseListModeState.value != ExpenseListMode.INDIVIDUAL) {
                expenseListModeState.value = ExpenseListMode.INDIVIDUAL
            }
            fetchAllExpenses()
        }
    }

    fun setListModeToMonthly() {
        with(uiState) {
            if (!isLoading && expenseListModeState.value != ExpenseListMode.MONTHLY) {
                expenseListModeState.value = ExpenseListMode.MONTHLY
            }
            fetchAllExpenses()
        }
    }

    fun fetchPreviousMonth() {
        if (!isLoading) {
            uiState.apply {
                startDateState.value = startDateState.value.minusMonths(1)
                endDateState.value = endDateState.value.minusMonths(1)
            }
            fetchAllExpenses()
        }
    }

    fun fetchNextMonth() {
        if (!isLoading) {
            uiState.apply {
                startDateState.value = startDateState.value.plusMonths(1)
                endDateState.value = endDateState.value.plusMonths(1)
            }
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

            val startDate = uiState.startDateState.value
            val endDate = uiState.endDateState.value
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
                        uiState.startDateState.value.plusMonths(1)
                    } else {
                        uiState.startDateState.value
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
                startDate = uiState.startDateState.value,
                endDate = uiState.endDateState.value
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
        uiState.apply {
            totalAmountState.value = summary.expensesByDate.values
                .sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
            totalCurrencyState.value = summary.expensesByDate.values.firstOrNull()
                ?.expenses?.firstOrNull()
                ?.currency?.currencyCode
                ?: ""
            val isSummaryNotEmpty = summary.expensesByDate.isNotEmpty()
            val hasAtLeastOneExpense = summary.expensesByDate.values.any { it.expenses.isNotEmpty() }
            expenseListResultState.value = if (isSummaryNotEmpty && hasAtLeastOneExpense) {
                ExpenseListResult.ResultWithData(summary)
            } else {
                ExpenseListResult.EmptyResult
            }
        }
    }
}
