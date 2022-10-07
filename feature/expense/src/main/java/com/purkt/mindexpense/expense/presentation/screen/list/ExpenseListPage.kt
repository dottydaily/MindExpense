package com.purkt.mindexpense.expense.presentation.screen.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.purkt.database.domain.model.Expense
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.list.component.DateLabel
import com.purkt.mindexpense.expense.presentation.screen.list.component.ExpenseCardInfo
import com.purkt.mindexpense.expense.presentation.screen.list.component.TotalAmountBox
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import com.purkt.ui.presentation.button.ui.component.AddButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Composable
fun ExpenseListPage(
    viewModel: ExpenseListViewModel = hiltViewModel(),
    navigator: ExpenseNavigator
) {
    // Launch this side-effect block after the composition is completed.
    // Use for waiting every state's transaction to be committed-
    // -before do some process that need to reference to those states
    LaunchedEffect(true) {
        viewModel.fetchAllExpenses()
    }

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
    val cardInfoItemsAsState by viewModel.cardInfoStateFlow.collectAsState()
    val totalAmount by viewModel.totalAmountState
    val totalCurrency by viewModel.totalCurrencyStringState
    BaseExpenseListPage(
        isLoading = isLoading,
        cardInfoList = cardInfoItemsAsState,
        totalAmount = totalAmount,
        totalCurrency = totalCurrency,
        navigator = navigator,
        onDeleteCard = viewModel::deleteExpense,
        onNavigateToAddExpensePage = viewModel::goToAddExpensePage
    )
}

@Composable
private fun BaseExpenseListPage(
    isLoading: Boolean = true,
    cardInfoList: List<ExpenseInfoItem>,
    totalAmount: Double,
    totalCurrency: String,
    navigator: ExpenseNavigator,
    onDeleteCard: (ExpenseInfoItem.ExpenseCardDetail) -> Unit = {},
    onNavigateToAddExpensePage: (ExpenseNavigator) -> Unit = {}
) {
    var targetStateToDelete by remember { mutableStateOf<ExpenseInfoItem.ExpenseCardDetail?>(null) }

    val primaryColor = MaterialTheme.colors.primaryVariant
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                if (cardInfoList.isNotEmpty()) {
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
                                .padding(24.dp)
                                .align(Alignment.Center),
                            totalAmount = totalAmount,
                            currency = totalCurrency,
                            backgroundColor = MaterialTheme.colors.background,
                            contentColor = primaryColor
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(
                            items = cardInfoList,
                            key = {
                                when (it) {
                                    is ExpenseInfoItem.ExpenseCardDetail -> {
                                        it.expense.id
                                    }
                                    is ExpenseInfoItem.ExpenseGroupDate -> {
                                        "${it.date.dayOfMonth}-${it.date.month}-${it.date.year}"
                                    }
                                }
                            }
                        ) {
                            when (it) {
                                is ExpenseInfoItem.ExpenseCardDetail -> {
                                    ExpenseCardInfo(
                                        cardDetail = it,
                                        onDeleteCard = {
                                            targetStateToDelete = it
                                        }
                                    )
                                }
                                is ExpenseInfoItem.ExpenseGroupDate -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        DateLabel(
                                            modifier = Modifier
                                                .align(Alignment.Center),
                                            dateString = it.dateString
                                        )
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter),
                        color = Color.Gray
                    )
                    AddButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 24.dp,
                                vertical = 8.dp
                            )
                            .align(Alignment.Center),
                        text = "Add new expense",
                        color = primaryColor,
                        onClick = {
                            onNavigateToAddExpensePage.invoke(navigator)
                        }
                    )
                }
            }
        }

        // Show dialog to confirm for deleting expense
        if (targetStateToDelete != null) {
            AlertDialog(
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
                        Text("Cancel", color = Color.DarkGray)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewExpenseScreenPage() {
    val data = listOf(
        ExpenseInfoItem.ExpenseGroupDate(LocalDate.of(2022, 7, 26)),
        ExpenseInfoItem.ExpenseCardDetail(
            Expense(
                id = 1,
                title = "Breakfast",
                description = "Eat breakfast with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
            ),
            isExpanded = false
        ),
        ExpenseInfoItem.ExpenseCardDetail(
            Expense(
                id = 2,
                title = "Lunch",
                description = "Eat lunch with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.of(2022, 7, 26, 18, 0, 0)
            ),
            isExpanded = false
        ),
        ExpenseInfoItem.ExpenseGroupDate(LocalDate.of(2022, 7, 25)),
        ExpenseInfoItem.ExpenseCardDetail(
            Expense(
                id = 3,
                title = "Dinner",
                description = "Eat dinner with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.of(2022, 7, 25, 18, 0, 0)
            ),
            isExpanded = false
        ),
        ExpenseInfoItem.ExpenseGroupDate(LocalDate.of(2022, 7, 24)),
        ExpenseInfoItem.ExpenseCardDetail(
            Expense(
                id = 4,
                title = "Dinner",
                description = "Eat dinner with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.of(2022, 7, 24, 18, 0, 0)
            ),
            isExpanded = false
        )
    )
    val cardDetails = data.filterIsInstance<ExpenseInfoItem.ExpenseCardDetail>()
    val totalAmount = cardDetails.sumOf { it.expense.amount }
    val totalCurrency = cardDetails.firstOrNull()?.expense?.currency?.currencyCode ?: ""
    BaseExpenseListPage(false, data, totalAmount, totalCurrency, ExpenseNavigator())
}

@Preview
@Composable
private fun PreviewExpenseScreenPageEmpty() {
    BaseExpenseListPage(false, emptyList(), 0.0, "THB", ExpenseNavigator())
}
