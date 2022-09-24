package com.purkt.mindexpense.expense.presentation.screen.list.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.purkt.database.domain.model.Expense
import java.time.LocalDateTime
import java.util.*

class ExpenseCardInfoState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val amount: Double = 0.00,
    val currency: Currency = Currency.getInstance(Locale.getDefault()),
    val dateTime: LocalDateTime = LocalDateTime.now(),
    isExpanded: Boolean = false
) {
    companion object {
        fun mapFromDomain(domain: Expense): ExpenseCardInfoState {
            return ExpenseCardInfoState(
                id = domain.id,
                title = domain.title,
                description = domain.description,
                amount = domain.amount,
                currency = domain.currency,
                dateTime = domain.dateTime,
                isExpanded = false
            )
        }
    }

    var isExpanded by mutableStateOf(isExpanded)
}
