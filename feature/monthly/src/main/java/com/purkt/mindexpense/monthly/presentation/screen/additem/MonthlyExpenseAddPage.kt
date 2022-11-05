package com.purkt.mindexpense.monthly.presentation.screen.additem

import android.app.TimePickerDialog
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.purkt.mindexpense.monthly.R
import com.purkt.mindexpense.monthly.presentation.screen.additem.state.AddRecurringExpenseStatus
import com.purkt.mindexpense.monthly.presentation.screen.additem.state.RecurringExpenseAddInfoState
import com.purkt.ui.presentation.button.ui.component.NormalEditText
import com.purkt.ui.presentation.button.ui.component.NumberEditText
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalTime

@Composable
fun MonthlyExpenseAddPage(
    targetExpenseId: Int? = null,
    viewModel: MonthlyExpenseAddViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    LaunchedEffect(Unit) {
        targetExpenseId?.let {
            viewModel.loadExpenseId(it)
        }
    }
    val addInfo by viewModel.addInfo
    val addExpenseStatus by viewModel.addStatusState
    BaseMonthlyExpenseAddPage(
        isUpdate = targetExpenseId != null,
        addInfo = addInfo,
        addExpenseStatus = addExpenseStatus,
        onClickBackButton = onClose,
        onClickSaveButton = viewModel::saveExpense
    )
}

@Composable
private fun BaseMonthlyExpenseAddPage(
    isUpdate: Boolean = false,
    addInfo: RecurringExpenseAddInfoState,
    addExpenseStatus: AddRecurringExpenseStatus,
    onClickBackButton: () -> Unit = {},
    onClickSaveButton: (RecurringExpenseAddInfoState) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    when (addExpenseStatus) {
        AddRecurringExpenseStatus.Failed -> {}
        AddRecurringExpenseStatus.Success -> onClickBackButton.invoke()
        AddRecurringExpenseStatus.Idle -> {}
    }
    LaunchedEffect(key1 = addInfo.title) {
        addInfo.isTitleInvalid = false
    }
    LaunchedEffect(key1 = addInfo.amount) {
        addInfo.isAmountInvalid = false
    }
    LaunchedEffect(key1 = addInfo.dayOfMonth) {
        addInfo.isDayOfMonthInvalid = false
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
                    text = if (isUpdate) "Edit recurring expense" else "Add new recurring expense",
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
                        label = stringResource(id = R.string.recurring_expense_label_title),
                        isError = addInfo.isTitleInvalid
                    )
                }
                if (addInfo.isTitleInvalid) {
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
                        label = stringResource(id = R.string.recurring_expense_label_description)
                    )
                }
                item {
                    NumberEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.amount,
                        onValueChange = { addInfo.amount = it },
                        label = stringResource(id = R.string.recurring_expense_label_amount),
                        isError = addInfo.isAmountInvalid
                    )
                }
                if (addInfo.isAmountInvalid) {
                    item {
                        Text(
                            text = "Amount must be greater than zero.",
                            color = MaterialTheme.colors.error,
                            fontSize = 12.sp
                        )
                    }
                }
                item {
                    NumberEditText(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = addInfo.dayOfMonth,
                        onValueChange = { addInfo.dayOfMonth = it },
                        label = stringResource(id = R.string.recurring_expense_label_day_of_month),
                        isError = addInfo.isDayOfMonthInvalid
                    )
                }
                if (addInfo.isDayOfMonthInvalid) {
                    item {
                        Text(
                            text = "Day of month must be the value between 1 to 31",
                            color = MaterialTheme.colors.error,
                            fontSize = 12.sp
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
private fun PreviewMonthlyExpenseAddPage() {
    val addInfo = RecurringExpenseAddInfoState()
    MindExpenseTheme {
        BaseMonthlyExpenseAddPage(
            addInfo = addInfo,
            addExpenseStatus = AddRecurringExpenseStatus.Idle
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMonthlyExpenseAddPageEditMode() {
    val addInfo = RecurringExpenseAddInfoState()
    MindExpenseTheme {
        BaseMonthlyExpenseAddPage(
            isUpdate = true,
            addInfo = addInfo,
            addExpenseStatus = AddRecurringExpenseStatus.Idle
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMonthlyExpenseAddPageShowError() {
    val addInfo = RecurringExpenseAddInfoState().apply {
        isTitleInvalid = true
        isAmountInvalid = true
    }
    MindExpenseTheme {
        BaseMonthlyExpenseAddPage(
            addInfo = addInfo,
            addExpenseStatus = AddRecurringExpenseStatus.Idle
        )
    }
}
