package com.purkt.mindexpense.expense.presentation.screen.additem

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.purkt.mindexpense.expense.R
import com.purkt.mindexpense.expense.presentation.screen.additem.state.AddExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.additem.state.ExpenseAddInfoState
import com.purkt.ui.presentation.button.ui.component.NormalEditText
import com.purkt.ui.presentation.button.ui.component.NumberEditText
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ExpenseAddPage(
    targetExpenseId: Int? = null,
    viewModel: ExpenseAddViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadAllExpenses()
        targetExpenseId?.let {
            viewModel.loadExpenseId(it)
        }
    }
    val addInfo by viewModel.addInfo
    val addExpenseStatus by viewModel.addStatusState
    BaseExpenseAddPage(
        isUpdate = targetExpenseId != null,
        addInfo = addInfo,
        addExpenseStatus = addExpenseStatus,
        onGetDateString = viewModel::getDateString,
        onGetTimeString = viewModel::getTimeString,
        onClickBackButton = onClose,
        onClickSaveButton = viewModel::saveExpense,
        onLoadTitleSuggestion = viewModel::loadTitleSuggestion,
        onLoadDescriptionSuggestion = viewModel::loadDescriptionSuggestion
    )
}

@Composable
private fun BaseExpenseAddPage(
    isUpdate: Boolean = false,
    addInfo: ExpenseAddInfoState,
    addExpenseStatus: AddExpenseStatus,
    onGetDateString: (dayOfMonth: Int, monthValue: Int, year: Int) -> String? = { _, _, _ -> "" },
    onGetTimeString: (hourOfDay: Int, minute: Int) -> String? = { _, _ -> "" },
    onClickBackButton: () -> Unit = {},
    onClickSaveButton: (ExpenseAddInfoState) -> Unit = {},
    onLoadTitleSuggestion: (keyword: String) -> Unit = {},
    onLoadDescriptionSuggestion: (keyword: String) -> Unit = {}
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
                .padding(horizontal = 24.dp)
        ) {
            var isExpandedTitleSuggestion by remember { mutableStateOf(true) }
            var isExpandedDescriptionSuggestion by remember { mutableStateOf(true) }
            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 96.dp),
                text = if (isUpdate) "Edit expense" else "Add new expense",
                color = MaterialTheme.colors.onBackground,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.Top)
                            .animateContentSize()
                    ) {
                        NormalEditText(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = addInfo.title,
                            onValueChange = {
                                addInfo.title = it
                                isExpandedTitleSuggestion = true
                                onLoadTitleSuggestion.invoke(it)
                            },
                            label = stringResource(id = R.string.expense_label_title),
                            isError = addInfo.isTitleInvalid.value
                        )
                        DropdownMenu(
                            expanded = isExpandedTitleSuggestion,
                            onDismissRequest = { isExpandedTitleSuggestion = false },
                            properties = PopupProperties(focusable = false)
                        ) {
                            addInfo.titleSuggestions.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        addInfo.title = it
                                        isExpandedTitleSuggestion = false
                                    }
                                ) {
                                    Text(it)
                                }
                            }
                        }
                    }
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.Top)
                            .animateContentSize()
                    ) {
                        NormalEditText(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = addInfo.description,
                            onValueChange = {
                                addInfo.description = it
                                isExpandedDescriptionSuggestion = true
                                onLoadDescriptionSuggestion.invoke(it)
                            },
                            label = stringResource(id = R.string.expense_label_description)
                        )
                        DropdownMenu(
                            expanded = isExpandedDescriptionSuggestion,
                            onDismissRequest = { isExpandedDescriptionSuggestion = false },
                            properties = PopupProperties(focusable = false)
                        ) {
                            addInfo.descriptionSuggestions.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        addInfo.description = it
                                        isExpandedDescriptionSuggestion = false
                                    }
                                ) {
                                    Text(it)
                                }
                            }
                        }
                    }
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
                                val currentDate = if (addInfo.date.isNotBlank()) {
                                    addInfo.getLocalDateTime().toLocalDate()
                                } else {
                                    LocalDate.now()
                                }
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
                                val currentTime = if (addInfo.time.isNotBlank()) {
                                    addInfo.getLocalDateTime().toLocalTime()
                                } else {
                                    LocalTime.now()
                                }
                                TimePickerDialog(
                                    currentContext,
                                    com.google.android.material.R.style.Theme_MaterialComponents_Dialog_Alert,
                                    { _, hour, minute ->
                                        val newTime = onGetTimeString.invoke(hour, minute)
                                        if (newTime != null) {
                                            addInfo.time = newTime
                                        }
                                    },
                                    currentTime.hour,
                                    currentTime.minute,
                                    true
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
            Row(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    onClick = { onClickBackButton.invoke() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colors.onBackground
                    )
                ) {
                    Text(
                        text = stringResource(id = com.purkt.ui.R.string.back),
                        fontSize = 16.sp
                    )
                }
                Button(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    shape = RoundedCornerShape(50),
                    onClick = { onClickSaveButton.invoke(addInfo) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    )
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 8.dp),
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save icon for save button"
                    )
                    Text(
                        text = stringResource(id = com.purkt.ui.R.string.save),
                        fontSize = 16.sp
                    )
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
private fun PreviewExpenseAddPageEditMode() {
    val addInfo = ExpenseAddInfoState()
    MindExpenseTheme {
        BaseExpenseAddPage(
            isUpdate = true,
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

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseAddPageShowTitleSuggestion() {
    val addInfo = ExpenseAddInfoState().apply {
        isTitleInvalid.value = true
        isAmountInvalid.value = true
        setNewTitleSuggestions(listOf("A", "B", "C", "D"))
    }
    MindExpenseTheme {
        BaseExpenseAddPage(
            addInfo = addInfo,
            addExpenseStatus = AddExpenseStatus.Idle
        )
    }
}
