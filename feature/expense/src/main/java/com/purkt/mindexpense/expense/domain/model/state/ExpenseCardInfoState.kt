package com.purkt.mindexpense.expense.domain.model.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDateTime
import java.util.*

class ExpenseCardInfoState(
    title: String = "",
    description: String = "",
    amount: Double = 0.00,
    currency: Currency = Currency.getInstance(Locale.getDefault()),
    dateTime: LocalDateTime = LocalDateTime.now(),
    isExpanded: Boolean = false
) {
    var title by mutableStateOf(title)
    var description by mutableStateOf(description)
    var amount by mutableStateOf(amount)
    var currency by mutableStateOf(currency)
    var dateTime by mutableStateOf(dateTime)
    var isExpanded by mutableStateOf(isExpanded)
}
