package com.purkt.mindexpense.expense.presentation.screen.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
                    items(cardInfoList) {
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

@Composable
private fun TotalAmountBox(
    modifier: Modifier = Modifier,
    totalAmount: Double,
    currency: String,
    backgroundColor: Color = MaterialTheme.colors.primaryVariant,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    val totalAmountString = DecimalFormat("#,##0.00").format(totalAmount)
    Card(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(percent = 50),
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = "$totalAmountString $currency",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun PreviewTotalAmountBox() {
    MindExpenseTheme {
        TotalAmountBox(totalAmount = 2097.0, currency = "THB")
    }
}

@Composable
private fun ExpenseCardInfo(
    state: ExpenseCardInfoState,
    onExpandedCard: (ExpenseCardInfoState) -> Unit = {}
) {
    val expense = state.expense
    val isExpanded = state.isExpanded
    val backgroundColor = if (isExpanded) {
        MaterialTheme.colors.surface
    } else {
        MaterialTheme.colors.primaryVariant
    }
    val contentColor = if (isExpanded) {
        MaterialTheme.colors.primaryVariant
    } else {
        contentColorFor(backgroundColor)
    }
    val border = if (isExpanded) {
        BorderStroke(2.dp, contentColor)
    } else {
        BorderStroke(0.dp, contentColor)
    }
    val shape = if (isExpanded) {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    } else {
        RoundedCornerShape(16.dp)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                border = border,
                shape = shape
            ),
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
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
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (isExpanded) {
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
                modifier = Modifier
                    .padding(top = 4.dp),
                onClick = {
                    onExpandedCard.invoke(state)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = contentColor
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
                dateTime = LocalDateTime.now()
            ),
            isExpanded = false
        ),
        ExpenseCardInfoState(
            Expense(
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
                title = "Lunch",
                description = "Eat lunch with friend at the mall",
                amount = 699.00,
                currency = Currency.getInstance("THB"),
                dateTime = LocalDateTime.now()
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
            dateTime = LocalDateTime.now()
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
            dateTime = LocalDateTime.now()
        ),
        isExpanded = true
    )
    MindExpenseTheme {
        ExpenseCardInfo(state = state)
    }
}
