package com.purkt.model.domain.model

import java.util.*

interface Expense {
    var title: String
    var description: String
    var amount: Double
    var currency: Currency
}
