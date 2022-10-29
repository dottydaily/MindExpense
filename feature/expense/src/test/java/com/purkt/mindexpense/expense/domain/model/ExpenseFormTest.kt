package com.purkt.mindexpense.expense.domain.model

import com.purkt.model.domain.model.Expense
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class ExpenseFormTest {
    @Test
    fun `Given we have valid currency codes, When setCurrencyCode() is called, then currency must be updated with target currency`() {
        // Given
        val mockCurrencyCodes = Currency.getAvailableCurrencies().map { it.currencyCode }.distinct()
        val mockForm = ExpenseForm()

        mockCurrencyCodes.forEach { code ->
            // When
            mockForm.setCurrencyCode(code)

            // Then
            assertEquals(code, mockForm.currency.currencyCode)
        }
    }

    @Test
    fun `Given we have invalid currency codes, When setCurrencyCode() is called, then currency must be updated with target currency`() {
        val mockCurrencyCodes = listOf(
            "",
            "!@#",
            "223344",
            "นี่คือรหัส"
        )
        val mockForm = ExpenseForm()
        val defaultCurrency = Currency.getInstance(Locale.getDefault())

        mockCurrencyCodes.forEach { code ->
            // When
            mockForm.setCurrencyCode(code)

            // Then
            assertEquals(defaultCurrency.currencyCode, mockForm.currency.currencyCode)
        }
    }

    @Test
    fun `Given we have valid titles, When isTitleValid() is called, then return true`() {
        // Given
        val mockTitles = listOf("Mock Title", "ข้อมูลจำลอง", "!@#$%")
        val mockForm = ExpenseForm()

        mockTitles.forEach { title ->
            // When
            mockForm.title = title
            // Then
            assertTrue(mockForm.isTitleValid())
        }
    }

    @Test
    fun `Given we have blank titles or some title has whitespace at the edge of text, When isTitleValid() is called, then return false`() {
        // Given
        val mockTitles = listOf("", "      ", "   Title", "Title ")
        val mockForm = ExpenseForm()

        mockTitles.forEach { title ->
            // When
            mockForm.title = title
            // Then
            assertFalse(mockForm.isTitleValid())
        }
    }

    @Test
    fun `Given we have valid amounts, When isAmountValid() is called, then return true`() {
        // Given
        val amounts = listOf(1.0, 0.1, 0.01, 0.001, 0.0001, 0.00001)
        val mockForm = ExpenseForm()

        amounts.forEach { amount ->
            // When
            mockForm.amount = amount
            // THen
            assertTrue(mockForm.isAmountValid())
        }
    }

    @Test
    fun `Given we have a amount that is less or equal than zero, When isAmountValid() is called, then return false`() {
        // Given
        val amounts = listOf(0.0, -0.00001, -0.0001, -0.001, -0.01, -0.1, -1.0)
        val mockForm = ExpenseForm()

        amounts.forEach { amount ->
            // When
            mockForm.amount = amount
            // THen
            assertFalse(mockForm.isAmountValid())
        }
    }

    @Test
    fun `Given we have all valid info, When createExpenseOrNull() is called, then return the created Expense object`() {
        // Given
        val mockForm = ExpenseForm(
            title = "Mock title",
            amount = 1.0
        )

        // When
        val actual = mockForm.createExpenseOrNull()

        // Then
        val expectedDateTime = LocalDateTime.of(mockForm.date, mockForm.time)
        val expected = Expense(
            id = mockForm.id,
            title = mockForm.title,
            description = mockForm.description,
            amount = mockForm.amount,
            currency = mockForm.currency,
            dateTime = expectedDateTime
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Given title is invalid, When createExpenseOrNull() is called, then return null`() {
        // Given
        val mockForm = ExpenseForm(
            title = "",
            amount = 1.0
        )

        // When
        val actual = mockForm.createExpenseOrNull()

        // Then
        assertNull(actual)
    }

    @Test
    fun `Given amount is invalid, When createExpenseOrNull() is called, then return null`() {
        // Given
        val mockForm = ExpenseForm(
            title = "Mock Title",
            amount = 0.0
        )

        // When
        val actual = mockForm.createExpenseOrNull()

        // Then
        assertNull(actual)
    }
}
