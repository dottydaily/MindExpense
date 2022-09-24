package com.purkt.mindexpense.expense.presentation.screen.additem.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ExpenseAddInfoState {
    var title: String by mutableStateOf("")
    var description: String by mutableStateOf("")
    var amount: String by mutableStateOf("0.00")
    var date: String by mutableStateOf("")
    var time: String by mutableStateOf("")
}
