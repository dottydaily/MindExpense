package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purkt.common.di.IoDispatcher
import com.purkt.database.domain.usecase.daterange.AddDateRangeUseCase
import com.purkt.database.domain.usecase.daterange.FindAllDateRangeUseCase
import com.purkt.database.domain.usecase.individualexpense.DeleteIndividualExpenseUseCase
import com.purkt.database.domain.usecase.individualexpense.FindAllIndividualExpensesUseCase
import com.purkt.database.domain.usecase.recurringexpense.FindAllRecurringExpensesUseCase
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.domain.model.ExpenseListMode
import com.purkt.mindexpense.expense.domain.model.ExpenseListResult
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseListPageUiState
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.DateRange
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
    private val addDateRangeUseCase: AddDateRangeUseCase,
    private val findAllDateRangeUseCase: FindAllDateRangeUseCase,
    private val findAllIndividualExpensesUseCase: FindAllIndividualExpensesUseCase,
    private val findAllRecurringExpensesUseCase: FindAllRecurringExpensesUseCase,
    private val deleteIndividualExpenseUseCase: DeleteIndividualExpenseUseCase
) : ViewModel() {
    private val _dateRangeList = mutableListOf<DateRange>()
    private var isLoading = true

    // Date related
    private var startDayEachMonth = 25

    /**
     * A [List] of all [DateRange] from database.
     */
    val dateRangeList: List<DateRange> = _dateRangeList

    val uiState = ExpenseListPageUiState()

    init {
        viewModelScope.launch {
            val allDateRanges = withContext(ioDispatcher) {
                findAllDateRangeUseCase.invoke().firstOrNull()
            } ?: emptyList()

            if (allDateRanges.isEmpty()) {
                val startDate = LocalDate.now().run {
                    withDayOfMonth(1)
                }
                val endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
                val initialDateRange = DateRange(
                    id = 1,
                    startDate = startDate,
                    endDate = endDate
                )
                val isAdded = withContext(ioDispatcher) {
                    addDateRangeUseCase.invoke(initialDateRange)
                }
                if (isAdded) {
                    addDateRange(initialDateRange)
                    uiState.run {
                        isShowDateRangeLeftButtonState.value = false
                        isShowDateRangeRightButtonState.value = false
                        currentDateRangeState.value = initialDateRange
                    }
                }
            } else {
                val today = LocalDate.now()
                val isNeedToCreateNewDateRange = allDateRanges.none {
                    today.isLocalDateInRangeOf(it.startDate, it.endDate)
                }

                if (isNeedToCreateNewDateRange) {
                    val startDate = today.withDayOfMonth(1)
                    val endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
                    val newDateRange = DateRange(
                        id = allDateRanges.maxOf { it.id } + 1,
                        startDate = startDate,
                        endDate = endDate
                    )
                    val isAdded = withContext(ioDispatcher) {
                        addDateRangeUseCase.invoke(newDateRange)
                    }
                    if (isAdded) {
                        uiState.apply {
                            val resultList = allDateRanges + newDateRange
                            val allDateRangeTypedArray = resultList.toTypedArray()
                            addDateRange(*allDateRangeTypedArray)

                            val targetDateRange = dateRangeList.last()
                            isShowDateRangeRightButtonState.value = false
                            currentDateRangeState.value = targetDateRange
                        }
                    }
                } else {
                    uiState.apply {
                        val allDateRangeTypedArray = allDateRanges.toTypedArray()
                        addDateRange(*allDateRangeTypedArray)

                        val targetDateRange = dateRangeList.last()
                        isShowDateRangeRightButtonState.value = false
                        currentDateRangeState.value = targetDateRange
                    }
                }
            }

            fetchAllExpenses()
            uiState.isInitializedState.value = true
        }
    }

    private fun addDateRange(vararg dateRanges: DateRange) {
        _dateRangeList.run {
            addAll(dateRanges)
            sortBy { it.endDate }
            sortBy { it.startDate }
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
        if (expenseListResult == ExpenseListResult.FOUND) {
            val stateList = uiState.dailyExpensesStateList

            val targetDailyExpenses = stateList.find { it.date == expense.dateTime.toLocalDate() }
            val targetExpense = targetDailyExpenses?.expenses
                ?.find { it.id == expense.id }
            if (targetDailyExpenses == null || targetExpense == null) {
                uiState.deleteStatusState.value = DeleteExpenseStatus.DataNotFoundInUi
            } else {
                val isDeleted = deleteIndividualExpenseUseCase.invoke(targetExpense)
                if (isDeleted) {
                    // Remove expense from the target daily expenses
                    uiState.removeExpense(targetExpense)

                    // Update each information
                    val newTotalAmount = stateList.sumOf { dailyExpense ->
                        dailyExpense.getTotalAmount()
                    }

                    uiState.apply {
                        totalAmountState.value = newTotalAmount
                        totalCurrencyState.value = stateList.firstOrNull()?.expenses?.firstOrNull()
                            ?.currency?.currencyCode
                            ?: ""
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

    fun fetchPreviousDateRange() {
        if (!isLoading) {
            uiState.apply {
                val currentIndex = dateRangeList.indexOf(currentDateRangeState.value)
                if (currentIndex > 0) {
                    val isFirstIndex = currentIndex - 1 == 0
                    val findTargetDateRange = dateRangeList[currentIndex - 1]
                    findTargetDateRange.let {
                        currentDateRangeState.value = it
                        isShowDateRangeRightButtonState.value = true
                        isShowDateRangeLeftButtonState.value = !isFirstIndex
                        fetchAllExpenses()
                    }
                }
            }
        }
    }

    fun fetchNextDateRange() {
        if (!isLoading) {
            uiState.apply {
                val currentIndex = dateRangeList.indexOf(currentDateRangeState.value)
                if (currentIndex < dateRangeList.lastIndex) {
                    val isLastIndex = (currentIndex + 1) == dateRangeList.lastIndex
                    val findTargetDateRange = dateRangeList[currentIndex + 1]
                    findTargetDateRange.let {
                        currentDateRangeState.value = it
                        isShowDateRangeLeftButtonState.value = true
                        isShowDateRangeRightButtonState.value = !isLastIndex
                        fetchAllExpenses()
                    }
                }
            }
        }
    }

    private suspend fun createIndividualExpenseSummary(list: List<IndividualExpense>): List<DailyExpenses> {
        return withContext(ioDispatcher) {
            val expensesGroupByDate = list.sortedByDescending { it.dateTime }
                .groupBy { it.dateTime.toLocalDate() }
                .mapValues {
                    DailyExpenses(
                        it.value.toMutableList(),
                        it.key
                    )
                }

            val startDate = uiState.currentDateRangeState.value.startDate
            val endDate = uiState.currentDateRangeState.value.endDate
            val filteredExpenses = expensesGroupByDate
                .filterKeys { it.isLocalDateInRangeOf(startDate, endDate) }
                .values

            return@withContext filteredExpenses.toList()
        }
    }

    private fun LocalDate.isLocalDateInRangeOf(startDate: LocalDate, endDate: LocalDate): Boolean {
        return isEqual(startDate) || (isAfter(startDate) && isBefore(endDate)) || isEqual(endDate)
    }

    private suspend fun createRecurringExpenseSummary(list: List<RecurringExpense>): List<DailyExpenses> {
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
                    val currentDateRange = uiState.currentDateRangeState.value
                    val newDate = try {
                            currentDateRange.startDate.withDayOfMonth(recurringExpense.dayOfMonth)
                    } catch (e: DateTimeException) {
                        Timber.e("Can't create new expense group by new date: ${e.message}")
                        currentDateRange.endDate
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

            return@withContext expensesGroupByDate.values.toList()
        }
    }

    private suspend fun Flow<List<DailyExpenses>>.collectExpenseSummaryFromTargetFlow(
        toDoAfterCollected: suspend () -> Unit = {}
    ) {
        collect { thisMonthExpenses ->
            setUiDataFromExpenseSummary(thisMonthExpenses)
            toDoAfterCollected.invoke()
        }
    }

    private fun setUiDataFromExpenseSummary(dailyExpensesList: List<DailyExpenses>) {
        uiState.apply {
            totalAmountState.value = dailyExpensesList
                .sumOf { dailyExpense -> dailyExpense.getTotalAmount() }
            totalCurrencyState.value = dailyExpensesList.firstOrNull()
                ?.expenses?.firstOrNull()
                ?.currency?.currencyCode
                ?: ""
            if (dailyExpensesList.isEmpty()) {
                if (expenseListResultState.value != ExpenseListResult.EMPTY) {
                    expenseListResultState.value = ExpenseListResult.EMPTY
                }
            } else {
                if (expenseListResultState.value != ExpenseListResult.FOUND) {
                    expenseListResultState.value = ExpenseListResult.FOUND
                }
            }
            uiState.setNewExpensesList(dailyExpensesList)
        }
    }
}
