import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.purkt.mindexpense.expense.R
import com.purkt.mindexpense.expense.presentation.navigation.ExpenseNavigator
import com.purkt.mindexpense.expense.presentation.screen.additem.ExpenseAddViewModel
import com.purkt.mindexpense.expense.presentation.screen.additem.state.AddExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.additem.state.ExpenseAddInfoState
import com.purkt.ui.presentation.button.ui.component.NormalEditText
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme

@Composable
fun ExpenseAddPage(
    viewModel: ExpenseAddViewModel = hiltViewModel(),
    navigator: ExpenseNavigator
) {
    val addExpenseStatus by viewModel.addResultStateFlow.collectAsState()
    BaseExpenseAddPage(
        addExpenseStatus = addExpenseStatus,
        navigator = navigator,
        onNavigateBack = viewModel::goBackToPreviousPage,
        onClickSaveButton = viewModel::addExpense
    )
}

@Composable
private fun BaseExpenseAddPage(
    addExpenseStatus: AddExpenseStatus,
    navigator: ExpenseNavigator,
    onNavigateBack: (ExpenseNavigator) -> Unit = {},
    onClickSaveButton: (ExpenseAddInfoState) -> Unit = {}
) {
    when (addExpenseStatus) {
        AddExpenseStatus.Failed -> {}
        AddExpenseStatus.Success -> {
            onNavigateBack.invoke(navigator)
        }
        AddExpenseStatus.Start -> {}
    }
    val addInfo by remember { mutableStateOf(ExpenseAddInfoState()) }
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    NormalEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.title,
                        onValueChange = { addInfo.title = it },
                        label = stringResource(id = R.string.expense_label_title)
                    )
                }
                item {
                    NormalEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.description,
                        onValueChange = { addInfo.description = it },
                        label = stringResource(id = R.string.expense_label_description)
                    )
                }
                item {
                    NormalEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.amount,
                        onValueChange = { addInfo.amount = it },
                        label = stringResource(id = R.string.expense_label_amount)
                    )
                }
                item {
                    NormalEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.date,
                        onValueChange = { addInfo.date = it },
                        label = stringResource(id = R.string.expense_label_date)
                    )
                }
                item {
                    NormalEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.time,
                        onValueChange = { addInfo.time = it },
                        label = stringResource(id = R.string.expense_label_time)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { onNavigateBack.invoke(navigator) },
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                ) {
                    Text(text = stringResource(id = com.purkt.ui.R.string.back))
                }
                Button(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { onClickSaveButton.invoke(addInfo) }
                ) {
                    Text(text = stringResource(id = com.purkt.ui.R.string.save))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewExpenseAddPage() {
    val navigator = ExpenseNavigator()
    MindExpenseTheme {
        BaseExpenseAddPage(
            addExpenseStatus = AddExpenseStatus.Start,
            navigator = navigator
        )
    }
}
