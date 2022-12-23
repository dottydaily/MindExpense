package com.purkt.mindexpense.expense.presentation.screen.list

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.purkt.common.domain.util.ComposeLifecycle
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.domain.model.ExpenseListMode
import com.purkt.mindexpense.expense.domain.model.ExpenseListResult
import com.purkt.mindexpense.expense.presentation.screen.additem.ExpenseAddActivity
import com.purkt.mindexpense.expense.presentation.screen.list.component.DailyExpensesDetail
import com.purkt.mindexpense.expense.presentation.screen.list.component.ExpenseListModeSelector
import com.purkt.mindexpense.expense.presentation.screen.list.component.MonthRangeBox
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseListPageUiState
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.IndividualExpense
import com.purkt.ui.presentation.button.ui.component.TotalAmountBox
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Composable
fun ExpenseListPage(
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    ComposeLifecycle.DoOnLifecycle(
        onResume = {
            viewModel.fetchAllExpenses()
        }
    )

    val context = LocalContext.current

    val uiState = remember { viewModel.uiState }
    val deleteStatus by uiState.deleteStatusState
    LaunchedEffect(key1 = deleteStatus) {
        when (deleteStatus) {
            DeleteExpenseStatus.DataNotFoundInUi -> {
                Toast.makeText(
                    context,
                    "Expense not found in the list",
                    Toast.LENGTH_SHORT
                ).show()
            }
            DeleteExpenseStatus.Failed -> {
                Toast.makeText(
                    context,
                    "Delete failed. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            DeleteExpenseStatus.Success -> {
                Toast.makeText(
                    context,
                    "Delete successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            DeleteExpenseStatus.Idle -> {}
        }
        viewModel.resetDeleteStatusToIdle()
    }

    BaseExpenseListPage(
        uiState = uiState,
        onSelectIndividualListMode = viewModel::setListModeToCommon,
        onSelectMonthlyListMode = viewModel::setListModeToMonthly,
        onDeleteCard = viewModel::deleteExpense,
        onChoosePreviousMonth = viewModel::fetchPreviousDateRange,
        onChooseNextMonth = viewModel::fetchNextDateRange
    )
}

@Composable
private fun BaseExpenseListPage(
    uiState: ExpenseListPageUiState,
    onSelectIndividualListMode: () -> Unit = {},
    onSelectMonthlyListMode: () -> Unit = {},
    onDeleteCard: (IndividualExpense) -> Unit = {},
    onChoosePreviousMonth: () -> Unit = {},
    onChooseNextMonth: () -> Unit = {}
) {
    var targetStateToDelete by remember { mutableStateOf<IndividualExpense?>(null) }

    val primaryColor = MaterialTheme.colors.primary
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val context = LocalContext.current
        val fabInteractionSource = remember { MutableInteractionSource() }
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(
                            bottom = 72.dp
                        ),
                    interactionSource = fabInteractionSource,
                    elevation = FloatingActionButtonDefaults.elevation(),
                    onClick = { startAddActivity(context) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add expense button",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        ) { padding ->
            val isInitialized by uiState.isInitializedState
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    val listMode by uiState.expenseListModeState
                    val expenseListResult by uiState.expenseListResultState
                    val totalAmount by uiState.totalAmountState
                    val totalCurrency by uiState.totalCurrencyState
                    TotalAmountBox(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.Start)
                            .animateContentSize(),
                        totalAmount = totalAmount,
                        currency = totalCurrency
                    )
                    ExpenseListModeSelector(
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 16.dp
                            )
                            .align(Alignment.Start),
                        mode = listMode,
                        onSelectIndividualListMode = onSelectIndividualListMode,
                        onSelectMonthlyListMode = onSelectMonthlyListMode
                    )
                    Crossfade(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        targetState = expenseListResult
                    ) { result ->
                        when (result) {
                            ExpenseListResult.EMPTY -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.Center),
                                        text = "No data",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.h6
                                    )
                                }
                            }
                            ExpenseListResult.LOADING -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                            ExpenseListResult.FOUND -> {
                                val dailyExpensesList = remember { uiState.dailyExpensesStateList }
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    items(items = dailyExpensesList, key = { it.date.toEpochDay() }) {
                                        DailyExpensesDetail(
                                            dailyExpenses = it,
                                            onEditExpense = { expenseId ->
                                                startAddActivity(context, expenseId)
                                            },
                                            onDeleteExpense = { targetExpense ->
                                                targetStateToDelete = targetExpense
                                            }
                                        )
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(120.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Crossfade(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.BottomCenter),
                    targetState = isInitialized
                ) { isDateSet ->
                    if (isDateSet) {
                        Card(
                            backgroundColor = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(50),
                            elevation = 2.dp
                        ) {
                            if (isInitialized) {
                                val currentDateRange by uiState.currentDateRangeState
                                val startDate = currentDateRange.startDate
                                val endDate = currentDateRange.endDate
                                val isShowLeftButton by uiState.isShowDateRangeLeftButtonState
                                val isShowRightButton by uiState.isShowDateRangeRightButtonState
                                MonthRangeBox(
                                    modifier = Modifier
                                        .animateContentSize(),
                                    startDate = startDate,
                                    endDate = endDate,
                                    contentColor = MaterialTheme.colors.onPrimary,
                                    isShowLeftButton = isShowLeftButton,
                                    isShowRightButton = isShowRightButton,
                                    onClickLeftArrow = onChoosePreviousMonth,
                                    onClickRightArrow = onChooseNextMonth
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show dialog to confirm for deleting expense
        if (targetStateToDelete != null) {
            AlertDialog(
                backgroundColor = MaterialTheme.colors.surface,
                onDismissRequest = { targetStateToDelete = null },
                title = {
                    Text(
                        text = "Do you want to delete this expense?",
                        fontWeight = FontWeight.Bold
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            targetStateToDelete?.let {
                                onDeleteCard.invoke(it)
                                targetStateToDelete = null
                            }
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colors.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            targetStateToDelete = null
                        }
                    ) {
                        Text("Cancel", color = MaterialTheme.colors.onSurface)
                    }
                }
            )
        }
    }
}

private fun startAddActivity(context: Context, targetExpenseId: Int? = null) {
    val intent = Intent(context, ExpenseAddActivity::class.java)
    targetExpenseId?.let {
        intent.putExtra(ExpenseAddActivity.INTENT_INTEGER_EXPENSE_ID, it)
    }
    context.startActivity(intent)
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseScreenPageCommon() {
    val mockData = mutableMapOf<LocalDate, DailyExpenses>(
        Pair(
            first = LocalDate.of(2022, 7, 26),
            second = DailyExpenses(
                expenses = mutableListOf(
                    IndividualExpense(
                        id = 1,
                        title = "Breakfast",
                        description = "Eat breakfast with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
                    ),
                    IndividualExpense(
                        id = 2,
                        title = "Lunch",
                        description = "Eat lunch with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
                    )
                ),
                date = LocalDate.of(2022, 7, 26)
            )
        ),
        Pair(
            first = LocalDate.of(2022, 7, 25),
            second = DailyExpenses(
                expenses = mutableListOf(
                    IndividualExpense(
                        id = 3,
                        title = "Dinner",
                        description = "Eat dinner with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 25, 18, 0, 0)
                    )
                ),
                date = LocalDate.of(2022, 7, 25)
            )
        ),
        Pair(
            first = LocalDate.of(2022, 7, 24),
            second = DailyExpenses(
                expenses = mutableListOf(
                    IndividualExpense(
                        id = 4,
                        title = "Dinner",
                        description = "Eat dinner with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 24, 18, 0, 0)
                    )
                ),
                date = LocalDate.of(2022, 7, 24)
            )
        )
    )
    val totalAmount = mockData.values.sumOf { it.getTotalAmount() }
    val totalCurrency = mockData.values.firstOrNull()?.expenses?.firstOrNull()?.currency?.currencyCode ?: ""
    val uiState = ExpenseListPageUiState().apply {
        expenseListResultState.value = ExpenseListResult.FOUND
        setNewExpensesList(mockData.values.toMutableStateList())
        totalAmountState.value = totalAmount
        totalCurrencyState.value = totalCurrency
        expenseListModeState.value = ExpenseListMode.INDIVIDUAL
        isInitializedState.value = true
    }
    MindExpenseTheme {
        BaseExpenseListPage(uiState)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseScreenPageMonthly() {
    val mockData = mutableMapOf<LocalDate, DailyExpenses>(
        Pair(
            first = LocalDate.of(2022, 7, 26),
            second = DailyExpenses(
                expenses = mutableListOf(
                    IndividualExpense(
                        id = -11,
                        title = "Breakfast",
                        description = "Eat breakfast with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
                    ),
                    IndividualExpense(
                        id = -12,
                        title = "Lunch",
                        description = "Eat lunch with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
                    )
                ),
                date = LocalDate.of(2022, 7, 26)
            )
        ),
        Pair(
            first = LocalDate.of(2022, 7, 25),
            second = DailyExpenses(
                expenses = mutableListOf(
                    IndividualExpense(
                        id = -13,
                        title = "Dinner",
                        description = "Eat dinner with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 25, 18, 0, 0)
                    )
                ),
                date = LocalDate.of(2022, 7, 25)
            )
        ),
        Pair(
            first = LocalDate.of(2022, 7, 24),
            second = DailyExpenses(
                expenses = mutableListOf(
                    IndividualExpense(
                        id = -14,
                        title = "Dinner",
                        description = "Eat dinner with friend at the mall",
                        amount = 699.00,
                        currency = Currency.getInstance("THB"),
                        dateTime = LocalDateTime.of(2022, 7, 24, 18, 0, 0)
                    )
                ),
                date = LocalDate.of(2022, 7, 24)
            )
        )
    )
    val totalAmount = mockData.values.sumOf { it.getTotalAmount() }
    val totalCurrency = mockData.values.firstOrNull()?.expenses?.firstOrNull()?.currency?.currencyCode ?: ""
    val uiState = ExpenseListPageUiState().apply {
        expenseListResultState.value = ExpenseListResult.FOUND
        setNewExpensesList(mockData.values.toMutableStateList())
        totalAmountState.value = totalAmount
        totalCurrencyState.value = totalCurrency
        expenseListModeState.value = ExpenseListMode.MONTHLY
        isInitializedState.value = true
    }
    MindExpenseTheme {
        BaseExpenseListPage(uiState)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseScreenPageEmpty() {
    val uiState = ExpenseListPageUiState().apply {
        expenseListResultState.value = ExpenseListResult.EMPTY
        totalAmountState.value = 0.0
        totalCurrencyState.value = "THB"
        expenseListModeState.value = ExpenseListMode.INDIVIDUAL
        isInitializedState.value = true
    }
    MindExpenseTheme {
        BaseExpenseListPage(uiState)
    }
}
