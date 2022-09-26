package com.purkt.mindexpense.expense.presentation.screen.additem.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExpenseAddInfoState {
    companion object {
        const val DATE_PATTERN = "dd-MMM-yyyy"
        const val TIME_PATTERN = "HH:mm"
    }
    var title: String by mutableStateOf("")
    var description: String by mutableStateOf("")
    var amount: String by mutableStateOf("0")
    var date: String by mutableStateOf("")
    var time: String by mutableStateOf("")
    var isTitleInvalid = mutableStateOf(false)
    var isAmountInvalid = mutableStateOf(false)

    init {
        val dateTime = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
        val timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN)
        date = dateFormatter.format(dateTime)
        time = timeFormatter.format(dateTime)
    }

    fun getLocalDateTime(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("$DATE_PATTERN $TIME_PATTERN")
        return LocalDateTime.parse("$date $time", formatter)
    }
}
