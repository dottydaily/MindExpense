package com.purkt.database.domain.model

import java.time.LocalDateTime

data class Expense(
    var id: Int = 0,
    var title: String = "",
    var amount: Double = 0.0,
    var currency: String = "",
    var dateTime: LocalDateTime = LocalDateTime.now()
)
