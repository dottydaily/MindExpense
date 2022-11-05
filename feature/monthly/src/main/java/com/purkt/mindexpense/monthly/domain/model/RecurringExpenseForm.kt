package com.purkt.mindexpense.monthly.domain.model

import com.purkt.common.domain.util.CurrencyUtil
import com.purkt.model.domain.model.RecurringExpense
import java.time.LocalTime
import java.util.*

class RecurringExpenseForm(
    val id: Int = 0,
    var title: String = "",
    var description: String = "",
    var amount: Double = 0.0,
    currency: Currency = Currency.getInstance(Locale.getDefault()),
    var dayOfMonth: Int = 0,
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
     * Check if [dayOfMonth] is valid or not.
     *
     * Valid dayOfMonth must be the value between 1 to 31.
     *
     * @return Return true if [dayOfMonth] is valid. Otherwise, return false.
     */
    fun isDayOfMonthValid(): Boolean {
        return dayOfMonth in 1..31
    }

    /**
     * Validate data and create [RecurringExpense] if all information is valid.
     * @return Return the target [RecurringExpense] if data is valid. Otherwise, return null.
     */
    fun createExpenseOrNull(): RecurringExpense? {
        return if (isTitleValid() && isAmountValid() && isDayOfMonthValid()) {
            RecurringExpense(
                id = id,
                title = title,
                description = description,
                amount = amount,
                currency = currency,
                dayOfMonth = dayOfMonth,
                time = time
            )
        } else {
            null
        }
    }
}
