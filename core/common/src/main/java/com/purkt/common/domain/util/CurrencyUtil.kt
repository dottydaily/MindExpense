package com.purkt.common.domain.util

import timber.log.Timber
import java.util.Currency

object CurrencyUtil {
    /**
     * Currency code of "Thai baht" in ISO4217 standard.
     */
    const val THAI_CURRENCY_CODE = "THB"

    /**
     * Return the [Currency] of Thai Baht.
     * @return The target [Currency] of Thai Baht.
     */
    fun getThaiCurrency(): Currency {
        return Currency.getInstance(THAI_CURRENCY_CODE)
    }

    /**
     * Create an instance of [Currency] by calling [Currency.getInstance] method
     * or return null if an exception is thrown from [Currency.getInstance] method.
     *
     * @see Currency.getInstance
     * @param currencyCode The target currency code.
     * @return Return the target [Currency] if it can be created. Otherwise, return null.
     */
    fun getInstanceOrNull(currencyCode: String): Currency? {
        return try {
            Currency.getInstance(currencyCode)
        } catch (e: IllegalArgumentException) {
            Timber.e(
                "Invalid currencyCode ($currencyCode) : ${e.message}" +
                    "\nUse \"$THAI_CURRENCY_CODE\" instead."
            )
            null
        } catch (e: Exception) {
            Timber.e(
                "Unexpected error when creating Currency : ${e.message}" +
                    "\nUse \"$THAI_CURRENCY_CODE\" instead."
            )
            null
        }
    }
}
