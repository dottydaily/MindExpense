package com.purkt.mindexpense.expense.presentation.additem

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.purkt.common.model.Event
import com.purkt.common.model.EventObserver
import com.purkt.database.domain.usecase.AddExpenseUseCase
import com.purkt.mindexpense.expense.presentation.screen.additem.state.ExpenseAddInfoState
import com.purkt.mindexpense.expense.presentation.screen.additem.ExpenseAddViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseAddViewModelTest {
    @get: Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

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
                date = "24/09/2022"
                time = "22:22:00"
            }
            val addSuccessEventSlot = slot<Event<Unit>>()
            val addSuccessEventObserverMock = mockk<EventObserver<Unit>>()
            every { addSuccessEventObserverMock.onChanged(capture(addSuccessEventSlot)) }

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addSuccessEvent.observeForever(addSuccessEventObserverMock)
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            assertEquals(Unit, addSuccessEventSlot.captured.peekContent())
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
                date = "24/09/2022"
                time = "22:22:00"
            }
            val addFailedEventSlot = slot<Event<Unit>>()
            val addFailedEventObserverMock = mockk<EventObserver<Unit>>()
            every { addFailedEventObserverMock.onChanged(capture(addFailedEventSlot)) }

            coEvery { addExpenseUseCase.invoke(any()) } returns false

            // When
            viewModel.run {
                addFailedEvent.observeForever(addFailedEventObserverMock)
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            assertEquals(Unit, addFailedEventSlot.captured.peekContent())
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
                date = "24/09/2022"
                time = "22:22:00"
            }
            val addFailedEventSlot = slot<Event<Unit>>()
            val addFailedEventObserverMock = mockk<EventObserver<Unit>>()
            every { addFailedEventObserverMock.onChanged(capture(addFailedEventSlot)) } returns Unit

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addFailedEvent.observeForever(addFailedEventObserverMock)
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            assertEquals(Unit, addFailedEventSlot.captured.peekContent())
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
                time = "23:20:00"
            }
            val addFailedEventSlot = slot<Event<Unit>>()
            val addFailedEventObserverMock = mockk<EventObserver<Unit>>()
            every { addFailedEventObserverMock.onChanged(capture(addFailedEventSlot)) } returns Unit

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addFailedEvent.observeForever(addFailedEventObserverMock)
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            assertEquals(Unit, addFailedEventSlot.captured.peekContent())
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
                date = "24/09/2022"
                time = "asdfasdf"
            }
            val addFailedEventSlot = slot<Event<Unit>>()
            val addFailedEventObserverMock = mockk<EventObserver<Unit>>()
            every { addFailedEventObserverMock.onChanged(capture(addFailedEventSlot)) } returns Unit

            coEvery { addExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                addFailedEvent.observeForever(addFailedEventObserverMock)
                addExpense(mockAddInfoState)
                advanceUntilIdle()
            }

            // Then
            assertEquals(Unit, addFailedEventSlot.captured.peekContent())
        }
    }
}
