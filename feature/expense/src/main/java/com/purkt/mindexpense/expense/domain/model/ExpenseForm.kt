package com.purkt.mindexpense.expense.domain.model

import com.purkt.common.domain.util.CurrencyUtil
import com.purkt.database.domain.model.Expense
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class ExpenseForm(
    val id: Int = 0,
    var title: String = "",
    var description: String = "",
    var amount: Double = 0.0,
    currency: Currency = Currency.getInstance(Locale.getDefault()),
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now()
) {
    var currency: Currency; private set

    init {
        this.currency = currency
    }

    /**
     * Set [currency] with the target currency code.
     * @param currencyCode The target currency code. It this isn't the valid currency code, then
     * it will create with [Currency] of the default [Locale] method instead.
     */
    fun setCurrencyCode(currencyCode: String) {
        currency = CurrencyUtil.getInstanceOrNull(currencyCode) ?: Currency.getInstance(Locale.getDefault())
    }

    /**
     * Check if [title] is valid or not.
     *
     * Valid title must be a text that isn't blank and don't have any whitespace at the edge of text.
     *
     * @return Return true if [title] is valid. Otherwise, return false.
     */
    fun isTitleValid(): Boolean {
        return title.matches(Regex("^[^\\s].+[^\\s]\$"))
    }

    /**
     * Check if [amount] is valid or not.
     *
     * Valid amount must be greater than zero.
     *
     * @return Return true if [amount] is valid. Otherwise, return false.
     */
    fun isAmountValid(): Boolean {
        return amount > 0.0
    }

    /**
     * Validate data and create [Expense] if all information is valid.
     * @return Return the target [Expense] if data is valid. Otherwise, return null.
     */
    fun createExpenseOrNull(): Expense? {
        return if (isTitleValid() && isAmountValid()) {
            Expense(
                id = id,
                title = title,
                description = description,
                amount = amount,
                currency = currency,
                dateTime = LocalDateTime.of(date, time)
            )
        } else {
            null
        }
    }
}
