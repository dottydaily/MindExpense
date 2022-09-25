package com.purkt.mindexpense.expense.presentation.screen.additem

import com.purkt.database.domain.usecase.AddExpenseUseCase
import com.purkt.mindexpense.expense.presentation.screen.additem.ExpenseAddViewModel
import com.purkt.mindexpense.expense.presentation.screen.additem.state.AddExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.additem.state.ExpenseAddInfoState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseAddViewModelTest {
    private val mockDateString = DateTimeFormatter.ofPattern(ExpenseAddInfoState.DATE_PATTERN)
        .format(LocalDate.of(2022, Month.SEPTEMBER, 25))
    private val mockTimeString = DateTimeFormatter.ofPattern(ExpenseAddInfoState.TIME_PATTERN)
        .format(LocalTime.of(2, 36))

    private val addExpenseUseCase: AddExpenseUseCase = mockk()
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: ExpenseAddViewModel

    @Before
    fun setup() = runTest {
        testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        viewModel = ExpenseAddViewModel(
            ioDispatcher = testDispatcher,
            addExpenseUseCase = addExpenseUseCase
        )
    }

    @After
    fun clear() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given we have all necessary information, when addExpense() is called and success, then success event must be triggered`() {
        runTest {
            // Given
            val mockAddInfoState = ExpenseAddInfoState().apply {
                title = "Mock Title"
                description = "Mock description"
                amount = "0.00"
                date = mockDateString
                time = mockTimeString
            }

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            val actual = viewModel.addStatusState.value
            assertEquals(AddExpenseStatus.Success, actual)
        }
    }

    @Test
    fun `Given we have all necessary information, when addExpense() is called and failed, then success event must be triggered`() {
        runTest {
            // Given
            val mockAddInfoState = ExpenseAddInfoState().apply {
                title = "Mock Title"
                description = "Mock description"
                amount = "0.00"
                date = mockDateString
                time = mockTimeString
            }

            coEvery { addExpenseUseCase.invoke(any()) } returns false

            // When
            viewModel.run {
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            val actual = viewModel.addStatusState.value
            assertEquals(AddExpenseStatus.Failed, actual)
        }
    }

    @Test
    fun `Given we have an invalid amount text, when addExpense() is called, then failed event must be triggered`() {
        runTest {
            // Given
            val mockAddInfoState = ExpenseAddInfoState().apply {
                title = "Mock Title"
                description = "Mock description"
                amount = "assdfasd"
                date = mockDateString
                time = mockTimeString
            }

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            val actual = viewModel.addStatusState.value
            assertEquals(AddExpenseStatus.Failed, actual)
        }
    }

    @Test
    fun `Given we have an invalid date string, when addExpense() is called, then failed event must be triggered`() {
        runTest {
            // Given
            val mockAddInfoState = ExpenseAddInfoState().apply {
                title = "Mock Title"
                description = "Mock description"
                amount = "0.00"
                date = "asfasd;f"
                time = mockTimeString
            }

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            val actual = viewModel.addStatusState.value
            assertEquals(AddExpenseStatus.Failed, actual)
        }
    }

    @Test
    fun `Given we have an invalid time string, when addExpense() is called, then failed event must be triggered`() {
        runTest {
            // Given
            val mockAddInfoState = ExpenseAddInfoState().apply {
                title = "Mock Title"
                description = "Mock description"
                amount = "0.00"
                date = mockDateString
                time = "asdfasdf"
            }

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            val actual = viewModel.addStatusState.value
            assertEquals(AddExpenseStatus.Failed, actual)
        }
    }
}
