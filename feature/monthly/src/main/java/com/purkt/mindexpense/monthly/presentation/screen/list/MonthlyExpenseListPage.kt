package com.purkt.mindexpense.monthly.presentation.screen.list

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
import com.purkt.mindexpense.monthly.domain.model.DeleteRecurringExpenseStatus
import com.purkt.mindexpense.monthly.presentation.screen.additem.MonthlyExpenseAddActivity
import com.purkt.mindexpense.monthly.presentation.screen.list.component.RecurringExpenseCardInfo
import com.purkt.mindexpense.monthly.presentation.screen.list.state.RecurringExpenseInfoItem
import com.purkt.model.domain.model.IndividualExpense
import com.purkt.model.domain.model.RecurringExpense
import com.purkt.ui.presentation.button.ui.component.TotalAmountBox
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalTime
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MonthlyExpenseListPage(
    viewModel: MonthlyExpenseListViewModel = hiltViewModel()
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
            DeleteRecurringExpenseStatus.DataNotFoundInUi -> {
                Toast.makeText(
                    context,
                    "Expense not found in the list",
                    Toast.LENGTH_SHORT
                ).show()
            }
            DeleteRecurringExpenseStatus.Failed -> {
                Toast.makeText(
                    context,
                    "Delete failed. Please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            DeleteRecurringExpenseStatus.Success -> {
                Toast.makeText(
                    context,
                    "Delete successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            DeleteRecurringExpenseStatus.Idle -> {}
        }
        viewModel.resetDeleteStatusToIdle()
    }
    val isLoading by viewModel.loadingState
    val recurringExpenses by viewModel.recurringExpenses.collectAsStateWithLifecycle()
    val totalAmount by viewModel.totalAmountState
    val totalCurrency by viewModel.totalCurrencyStringState
    BaseMonthlyExpenseListPage(
        isLoading = isLoading,
        expenses = recurringExpenses,
        totalAmount = totalAmount,
        currencyString = totalCurrency,
        onDeleteExpenseListener = viewModel::deleteExpense
    )
}

@Composable
private fun BaseMonthlyExpenseListPage(
    isLoading: Boolean = false,
    expenses: List<RecurringExpense>,
    totalAmount: Double,
    currencyString: String,
    onDeleteExpenseListener: (expense: RecurringExpense) -> Unit = {}
) {
    var targetStateToDelete by remember { mutableStateOf<RecurringExpense?>(null) }

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
                    interactionSource = fabInteractionSource,
                    elevation = FloatingActionButtonDefaults.elevation(),
                    onClick = { startAddActivity(context) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add icon for Monthly List page",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                    if (expenses.isEmpty()) {
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
                    } else {
                        TotalAmountBox(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.Start),
                            totalAmount = totalAmount,
                            currency = currencyString
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val cardInfoItems = expenses.map { RecurringExpenseInfoItem(it) }
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            items(
                                items = cardInfoItems,
                                key = { it.recurringExpense.id }
                            ) { cardInfoItem ->
                                RecurringExpenseCardInfo(
                                    cardInfo = cardInfoItem,
                                    onEditListener = { startAddActivity(context, cardInfoItem.recurringExpense.id) },
                                    onDeleteListener = { targetStateToDelete = cardInfoItem.recurringExpense }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(100.dp))
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
                                onDeleteExpenseListener.invoke(it)
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
    val intent = Intent(context, MonthlyExpenseAddActivity::class.java)
    targetExpenseId?.let {
        intent.putExtra(MonthlyExpenseAddActivity.INTENT_INTEGER_RECURRING_EXPENSE_ID, it)
    }
    context.startActivity(intent)
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMonthlyExpenseListPage() {
    val mockItems = listOf(
        RecurringExpense(
            id = 1,
            title = "Youtube Premium",
            description = "Shared with friends",
            amount = 209.00,
            currency = Currency.getInstance("THB"),
            dayOfMonth = 1
        ),
        RecurringExpense(
            id = 2,
            title = "Spotify Premium",
            description = "Shared with friends",
            amount = 209.00,
            currency = Currency.getInstance("THB"),
            dayOfMonth = 9
        ),
        RecurringExpense(
            id = 3,
            title = "Discord Nitro",
            amount = 350.00,
            currency = Currency.getInstance("THB"),
            dayOfMonth = 15
        )
    )
    val totalAmount = mockItems.sumOf { it.amount }
    MindExpenseTheme {
        Surface {
            BaseMonthlyExpenseListPage(
                expenses = mockItems,
                totalAmount = totalAmount,
                currencyString = "THB"
            )
        }
    }
}
