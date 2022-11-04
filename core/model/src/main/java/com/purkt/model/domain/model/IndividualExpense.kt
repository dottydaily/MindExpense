package com.purkt.model.domain.model

import java.time.LocalDateTime
import java.util.*

data class IndividualExpense(
    var id: Int = 0,
    override var title: String = "",
    override var description: String = "",
    override var amount: Double = 0.0,
    override var currency: Currency = Currency.getInstance(Locale.getDefault()),
    var dateTime: LocalDateTime = LocalDateTime.now()
) : Expense {
    companion object {
        const val ID_FOR_RECURRING_EXPENSE: Int = -1
    }
}
