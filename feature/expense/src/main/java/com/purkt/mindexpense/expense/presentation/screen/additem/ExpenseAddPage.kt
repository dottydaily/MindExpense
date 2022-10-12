import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.purkt.mindexpense.expense.R
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
    onClose: () -> Unit
) {
    val addInfo by remember { mutableStateOf(ExpenseAddInfoState()) }
    val addExpenseStatus by viewModel.addStatusState
    BaseExpenseAddPage(
        addInfo = addInfo,
        addExpenseStatus = addExpenseStatus,
        onGetDateString = viewModel::getDateString,
        onGetTimeString = viewModel::getTimeString,
        onClickBackButton = onClose,
        onClickSaveButton = viewModel::saveExpense
    )
}

@Composable
private fun BaseExpenseAddPage(
    addInfo: ExpenseAddInfoState,
    addExpenseStatus: AddExpenseStatus,
    onGetDateString: (dayOfMonth: Int, monthValue: Int, year: Int) -> String? = { _, _, _ -> "" },
    onGetTimeString: (hourOfDay: Int, minute: Int) -> String? = { _, _ -> "" },
    onClickBackButton: () -> Unit = {},
    onClickSaveButton: (ExpenseAddInfoState) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    when (addExpenseStatus) {
        AddExpenseStatus.Failed -> {}
        AddExpenseStatus.Success -> onClickBackButton.invoke()
        AddExpenseStatus.Idle -> {}
    }
    LaunchedEffect(key1 = addInfo.title) {
        addInfo.isTitleInvalid.value = false
    }
    LaunchedEffect(key1 = addInfo.amount) {
        addInfo.isAmountInvalid.value = false
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { focusManager.clearFocus() }
            ),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)
                    .padding(24.dp)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    text = "Add new expense",
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
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
                item {
                    NormalEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.title,
                        onValueChange = { addInfo.title = it },
                        label = stringResource(id = R.string.expense_label_title),
                        isError = addInfo.isTitleInvalid.value
                    )
                }
                if (addInfo.isTitleInvalid.value) {
                    item {
                        Text(
                            text = "Title must have at least 1 character and must be trimmed.",
                            color = MaterialTheme.colors.error,
                            fontSize = 12.sp
                        )
                    }
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
                        label = stringResource(id = R.string.expense_label_amount),
                        isError = addInfo.isAmountInvalid.value
                    )
                }
                if (addInfo.isAmountInvalid.value) {
                    item {
                        Text(
                            text = "Amount must be greater than zero.",
                            color = MaterialTheme.colors.error,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    val currentContext = LocalContext.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
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
                                focusManager.clearFocus()
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
                    .background(MaterialTheme.colors.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = { onClickBackButton.invoke() },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = MaterialTheme.colors.onPrimary
                        )
                    ) {
                        Text(text = stringResource(id = com.purkt.ui.R.string.back))
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = { onClickSaveButton.invoke(addInfo) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        )
                    ) {
                        Text(text = stringResource(id = com.purkt.ui.R.string.save))
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseAddPage() {
    val addInfo = ExpenseAddInfoState()
    MindExpenseTheme {
        BaseExpenseAddPage(
            addInfo = addInfo,
            addExpenseStatus = AddExpenseStatus.Idle
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseAddPageShowError() {
    val addInfo = ExpenseAddInfoState().apply {
        isTitleInvalid.value = true
        isAmountInvalid.value = true
    }
    MindExpenseTheme {
        BaseExpenseAddPage(
            addInfo = addInfo,
            addExpenseStatus = AddExpenseStatus.Idle
        )
    }
}
