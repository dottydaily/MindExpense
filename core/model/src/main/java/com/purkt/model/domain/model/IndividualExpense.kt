package com.purkt.model.domain.model

import java.time.LocalDateTime
import java.util.*

data class IndividualExpense(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var amount: Double = 0.0,
    var currency: Currency = Currency.getInstance(Locale.getDefault()),
    var dateTime: LocalDateTime = LocalDateTime.now()
)
