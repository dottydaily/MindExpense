import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.purkt.ui.presentation.button.ui.component.NumberEditText
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ExpenseAddPage(
    viewModel: ExpenseAddViewModel = hiltViewModel(),
    navigator: ExpenseNavigator
) {
    val addInfo by remember { mutableStateOf(ExpenseAddInfoState()) }
    val addExpenseStatus by viewModel.addStatusState
    BaseExpenseAddPage(
        addInfo = addInfo,
        addExpenseStatus = addExpenseStatus,
        onGetDateString = viewModel::getDateString,
        onGetTimeString = viewModel::getTimeString,
        navigator = navigator,
        onNavigateBack = viewModel::goBackToPreviousPage,
        onClickSaveButton = viewModel::addExpense
    )
}

@Composable
private fun BaseExpenseAddPage(
    addInfo: ExpenseAddInfoState,
    addExpenseStatus: AddExpenseStatus,
    onGetDateString: (dayOfMonth: Int, monthValue: Int, year: Int) -> String? = { _, _, _ -> "" },
    onGetTimeString: (hourOfDay: Int, minute: Int) -> String? = { _, _ -> "" },
    navigator: ExpenseNavigator,
    onNavigateBack: (ExpenseNavigator) -> Unit = {},
    onClickSaveButton: (ExpenseAddInfoState) -> Unit = {}
) {
    when (addExpenseStatus) {
        AddExpenseStatus.Failed -> {}
        AddExpenseStatus.Success -> {
            onNavigateBack.invoke(navigator)
        }
        AddExpenseStatus.Idle -> {}
    }
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
                    .padding(horizontal = 24.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
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
                    NumberEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.amount,
                        onValueChange = { addInfo.amount = it },
                        label = stringResource(id = R.string.expense_label_amount)
                    )
                }
                item {
                    val currentContext = LocalContext.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val currentDate = LocalDate.now()
                                DatePickerDialog(
                                    currentContext,
                                    { _, year, monthValueCalender, dayOfMonth ->
                                        val newDate = onGetDateString.invoke(
                                            dayOfMonth,
                                            monthValueCalender,
                                            year
                                        )
                                        if (newDate != null) {
                                            addInfo.date = newDate
                                        }
                                    },
                                    currentDate.year,
                                    currentDate.monthValue - 1,
                                    currentDate.dayOfMonth
                                ).show()
                            }
                    ) {
                        NormalEditText(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = addInfo.date,
                            onValueChange = { addInfo.date = it },
                            label = stringResource(id = R.string.expense_label_date),
                            isReadOnly = true
                        )
                    }
                }
                item {
                    val currentContext = LocalContext.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val currentTime = LocalTime.now()
                                TimePickerDialog(
                                    currentContext,
                                    { _, hour, minute ->
                                        val newTime = onGetTimeString.invoke(hour, minute)
                                        if (newTime != null) {
                                            addInfo.time = newTime
                                        }
                                    },
                                    currentTime.hour,
                                    currentTime.minute,
                                    false
                                ).show()
                            }
                    ) {
                        NormalEditText(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = addInfo.time,
                            onValueChange = { addInfo.time = it },
                            label = stringResource(id = R.string.expense_label_time),
                            isReadOnly = true
                        )
                    }
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
                    color = Color.Gray,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .align(Alignment.BottomCenter),
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
}

@Preview
@Composable
private fun PreviewExpenseAddPage() {
    val navigator = ExpenseNavigator()
    val addInfo = ExpenseAddInfoState()
    MindExpenseTheme {
        BaseExpenseAddPage(
            addInfo = addInfo,
            addExpenseStatus = AddExpenseStatus.Idle,
            navigator = navigator
        )
    }
}
