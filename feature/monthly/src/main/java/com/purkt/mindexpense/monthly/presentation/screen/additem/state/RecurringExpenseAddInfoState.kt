package com.purkt.mindexpense.monthly.presentation.screen.additem.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class RecurringExpenseAddInfoState {
    companion object {
        const val TIME_PATTERN = "HH:mm"
    }
    var recurringExpenseId: Int = 0
    var title: String by mutableStateOf("")
    var description: String by mutableStateOf("")
    var amount: String by mutableStateOf("")
    var dayOfMonth: String by mutableStateOf("")
    var time: String by mutableStateOf("")
    var isTitleInvalid by mutableStateOf(false)
    var isAmountInvalid by mutableStateOf(false)
    var isDayOfMonthInvalid by mutableStateOf(false)

    init {
        val now = LocalDateTime.now()
        dayOfMonth = now.dayOfMonth.toString()
        val timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN)
        time = timeFormatter.format(now)
    }

    fun getLocalTime(): LocalTime {
        val formatter = DateTimeFormatter.ofPattern(TIME_PATTERN)
        return LocalTime.parse(time, formatter)
    }
}
