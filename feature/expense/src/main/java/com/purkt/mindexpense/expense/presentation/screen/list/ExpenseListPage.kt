package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.purkt.database.domain.model.Expense
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseCardInfoState
import com.purkt.ui.presentation.button.ui.component.AddButton
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ExpenseListPage(
    viewModel: ExpenseListViewModel = hiltViewModel(),
    navigator: ExpenseNavigator
) {
    rememberCoroutineScope {
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
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
                val totalAmount = DecimalFormat("#,##0.00").format(cardInfoList.sumOf { it.expense.amount })
                val currency = cardInfoList.firstOrNull()?.expense?.currency?.currencyCode ?: ""
                Text(
                    modifier = Modifier
                        .padding(
                            vertical = 16.dp
                        )
                        .align(Alignment.CenterHorizontally),
                    text = "$totalAmount $currency",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cardInfoList) {
                        ExpenseCardInfo(
                            state = it,
                            onExpandedCard = onExpandedCard
                        )
                    }
                }
            }
            AddButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                text = "Add new expense",
                onClick = {
                    onNavigateToAddExpensePage.invoke(navigator)
                }
            )
        }
    }
}

@Composable
private fun ExpenseCardInfo(
    state: ExpenseCardInfoState,
    onExpandedCard: (ExpenseCardInfoState) -> Unit = {}
) {
    val expense = state.expense
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val maxLinesTitle = if (state.isExpanded) Int.MAX_VALUE else 1
                Text(
                    text = expense.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = maxLinesTitle
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    val amountFormatted = DecimalFormat("#,##0.00").format(expense.amount)
                    val currencyDisplayName = expense.currency.currencyCode
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM, dd, yyyy HH:mm")
                    val dateTimeString = dateTimeFormatter.format(expense.dateTime)
                    Text(
                        text = "$amountFormatted $currencyDisplayName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateTimeString,
                        fontSize = 10.sp
                    )
                }
            }
            if (state.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = state.expense.description
                    )
                }
            }
            TextButton(
                onClick = {
                    onExpandedCard.invoke(state)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                val content = if (state.isExpanded) "Hide info" else "Show info"
                Text(
                    text = content
                )
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
                title = "Lunch",
                description = "Eat lunch with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.now(),
            ),
            isExpanded = false
        ),
        ExpenseCardInfoState(
            Expense(
                title = "Lunch",
                description = "Eat lunch with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.now(),
            ),
            isExpanded = false
        ),
        ExpenseCardInfoState(
            Expense(
                title = "Lunch",
                description = "Eat lunch with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.now(),
            ),
            isExpanded = false
        )
    )
    BaseExpenseListPage(false, data, ExpenseNavigator())
}

@Preview
@Composable
private fun PreviewExpenseCardInfoCollapse() {
    val state = ExpenseCardInfoState(
        Expense(
            title = "Lunch",
            description = "Eat lunch with friend at the mall",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now(),
        ),
        isExpanded = false
    )
    MindExpenseTheme {
        ExpenseCardInfo(state = state)
    }
}

@Preview
@Composable
private fun PreviewExpenseCardInfoExpanded() {
    val state = ExpenseCardInfoState(
        Expense(
            title = "Lunch",
            description = "Eat lunch with friend at the mall",
            amount = 699.00,
            currency = Currency.getInstance("THB"),
            dateTime = LocalDateTime.now(),
        ),
        isExpanded = true
    )
    MindExpenseTheme {
        ExpenseCardInfo(state = state)
    }
}
