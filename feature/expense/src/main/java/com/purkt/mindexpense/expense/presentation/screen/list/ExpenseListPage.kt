package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.purkt.database.domain.model.Expense
import com.purkt.mindexpense.expense.presentation.screen.list.component.ExpenseCardInfo
import com.purkt.mindexpense.expense.presentation.screen.list.component.TotalAmountBox
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseCardInfoState
import com.purkt.ui.presentation.button.ui.component.AddButton
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
    val isLoading by viewModel.loadingState
    val cardInfoListAsState by viewModel.cardInfoStateFlow.collectAsState()
    BaseExpenseListPage(
        isLoading = isLoading,
        cardInfoList = cardInfoListAsState,
        navigator = navigator,
        onExpandedCard = viewModel::changeCardInfoExpandedState,
        onNavigateToAddExpensePage = viewModel::goToAddExpensePage
    )
}

@Composable
private fun BaseExpenseListPage(
    isLoading: Boolean = true,
    cardInfoList: List<ExpenseCardInfoState>,
    navigator: ExpenseNavigator,
    onExpandedCard: (ExpenseCardInfoState) -> Unit = {},
    onNavigateToAddExpensePage: (ExpenseNavigator) -> Unit = {}
) {
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
                val totalAmount = cardInfoList.sumOf { it.expense.amount }
                val currency = cardInfoList.firstOrNull()?.expense?.currency?.currencyCode ?: ""
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
                        currency = currency,
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
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    items(items = cardInfoList, key = { it.expense.id }) {
                        ExpenseCardInfo(
                            state = it,
                            onExpandedCard = onExpandedCard
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
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
    }
}

@Preview
@Composable
private fun PreviewExpenseScreenPage() {
    val data = listOf(
        ExpenseCardInfoState(
            Expense(
                id = 1,
                title = "Breakfast",
                description = "Eat breakfast with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.now()
            ),
            isExpanded = false
        ),
        ExpenseCardInfoState(
            Expense(
                id = 2,
                title = "Lunch",
                description = "Eat lunch with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.now()
            ),
            isExpanded = false
        ),
        ExpenseCardInfoState(
            Expense(
                id = 3,
                title = "Dinner",
                description = "Eat dinner with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.now()
            ),
            isExpanded = false
        )
    )
    BaseExpenseListPage(false, data, ExpenseNavigator())
}
