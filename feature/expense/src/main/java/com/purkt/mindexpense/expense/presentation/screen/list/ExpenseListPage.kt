package com.purkt.mindexpense.expense.presentation.screen.list

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.purkt.common.domain.util.ComposeLifecycle
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.additem.ExpenseAddActivity
import com.purkt.mindexpense.expense.presentation.screen.list.component.DailyDetailTitle
import com.purkt.mindexpense.expense.presentation.screen.list.component.ExpenseCardInfo
import com.purkt.mindexpense.expense.presentation.screen.list.component.MonthRangeBox
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import com.purkt.model.domain.model.DailyExpenses
import com.purkt.model.domain.model.ExpenseSummary
import com.purkt.model.domain.model.IndividualExpense
import com.purkt.ui.presentation.button.ui.component.TotalAmountBox
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ExpenseListPage(
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    ComposeLifecycle.DoOnLifecycle(
        onResume = {
            viewModel.fetchAllExpenses()
        }
    )

    val deleteStatus by viewModel.deleteStatusState
    val context = LocalContext.current
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
    val isLoading by viewModel.loadingState
    val monthlyExpenses by viewModel.expenseSummaryFlow.collectAsStateWithLifecycle()
    val totalAmount by viewModel.totalAmountState
    val totalCurrency by viewModel.totalCurrencyStringState
    BaseExpenseListPage(
        isLoading = isLoading,
        expenseSummary = monthlyExpenses,
        totalAmount = totalAmount,
        totalCurrency = totalCurrency,
        onDeleteCard = viewModel::deleteExpense,
        onChoosePreviousMonth = viewModel::fetchPreviousMonth,
        onChooseNextMonth = viewModel::fetchNextMonth
    )
}

@Composable
private fun BaseExpenseListPage(
    isLoading: Boolean = true,
    expenseSummary: ExpenseSummary?,
    totalAmount: Double,
    totalCurrency: String,
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
                            bottom = 60.dp
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    if (expenseSummary != null && expenseSummary.expensesByDate.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    color = primaryColor
                                )
                        ) {
                            TotalAmountBox(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                                totalAmount = totalAmount,
                                currency = totalCurrency,
                                backgroundColor = MaterialTheme.colors.background
                            )
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            expenseSummary.expensesByDate.forEach { (localDate, dailyExpenses) ->
                                val dateDetail = ExpenseInfoItem.ExpenseDateDetail(localDate)
                                item {
                                    Row(
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        DailyDetailTitle(
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically),
                                            expenses = dailyExpenses.expenses,
                                            dateDetail = dateDetail
                                        )
                                    }
                                }
                                items(items = dailyExpenses.expenses, key = { it.id }) { expense ->
                                    ExpenseCardInfo(
                                        cardDetail = ExpenseInfoItem.ExpenseCardDetail(expense),
                                        onEditExpense = { startAddActivity(context, expense.id) },
                                        onDeleteCard = { targetStateToDelete = expense }
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(120.dp))
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
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
                }

                if (expenseSummary != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(primaryColor)
                    ) {
                        MonthRangeBox(
                            modifier = Modifier
                                .align(Alignment.Center),
                            startDate = expenseSummary.startDate,
                            endDate = expenseSummary.endDate,
                            contentColor = MaterialTheme.colors.onPrimary,
                            onClickLeftArrow = onChoosePreviousMonth,
                            onClickRightArrow = onChooseNextMonth
                        )
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
private fun PreviewExpenseScreenPage() {
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
    val expenseSummary = ExpenseSummary(
        expensesByDate = mockData,
        startDate = LocalDate.of(2022, Month.JULY, 25),
        endDate = LocalDate.of(2022, Month.AUGUST, 24)
    )
    val totalAmount = mockData.values.sumOf { it.getTotalAmount() }
    val totalCurrency = mockData.values.firstOrNull()?.expenses?.firstOrNull()?.currency?.currencyCode ?: ""
    MindExpenseTheme {
        BaseExpenseListPage(false, expenseSummary, totalAmount, totalCurrency)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseScreenPageEmpty() {
    val expenseSummary = ExpenseSummary(
        expensesByDate = mutableMapOf(),
        startDate = LocalDate.of(2022, Month.JULY, 25),
        endDate = LocalDate.of(2022, Month.AUGUST, 24)
    )
    MindExpenseTheme {
        BaseExpenseListPage(false, expenseSummary, 0.0, "THB")
    }
}
