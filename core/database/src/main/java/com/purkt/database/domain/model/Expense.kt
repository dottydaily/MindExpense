package com.purkt.database.domain.model

import java.time.LocalDateTime
import java.util.Currency

data class Expense(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var amount: Double = 0.0,
    var currency: Currency = Currency.getInstance("THB"),
    var dateTime: LocalDateTime = LocalDateTime.now()
)
