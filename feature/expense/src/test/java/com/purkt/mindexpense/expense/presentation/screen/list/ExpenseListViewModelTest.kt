package com.purkt.mindexpense.expense.presentation.screen.list

import com.purkt.database.domain.model.Expense
import com.purkt.database.domain.usecase.DeleteExpenseUseCase
import com.purkt.database.domain.usecase.FindAllExpensesUseCase
import com.purkt.mindexpense.expense.domain.model.DeleteExpenseStatus
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseCardInfoState
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseListViewModelTest {
    private val findAllExpensesUseCase: FindAllExpensesUseCase = mockk()
    private val deleteExpenseUseCase: DeleteExpenseUseCase = mockk()
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var viewModel: ExpenseListViewModel

    @Before
    fun setup() = runTest {
        testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        viewModel = ExpenseListViewModel(
            ioDispatcher = testDispatcher,
            findAllExpensesUseCase = findAllExpensesUseCase,
            deleteExpenseUseCase = deleteExpenseUseCase
        )
    }

    @After
    fun clear() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When fetchAllExpenses() is called, then cardInfoStateFlow must be updated`() {
        runTest {
            // Given
            val mockExpenses = listOf(
                Expense(id = 0),
                Expense(id = 1)
            )
            val mockFlow = flow {
                emit(mockExpenses)
            }
            coEvery { findAllExpensesUseCase.invoke() } returns mockFlow

            assertTrue(viewModel.cardInfoStateFlow.value.isEmpty())
            assertTrue(viewModel.loadingState.value)

            // When
            viewModel.run {
                fetchAllExpenses()
                advanceUntilIdle()
            }

            // Then
            val expected = mockExpenses.map {
                ExpenseCardInfoState(it)
            }
            val actual = viewModel.cardInfoStateFlow.value
            actual.forEachIndexed { index, expenseCardInfoState ->
                assertEquals(expected[index].expense, expenseCardInfoState.expense)
                assertEquals(expected[index].isExpanded, expenseCardInfoState.isExpanded)
            }
            assertFalse(viewModel.loadingState.value)
        }
    }

    @Test
    fun `Given we don't have any data, When fetchAllExpenses() is called, then cardInfoStateFlow must be updated`() {
        runTest {
            // Given
            val mockFlow = flow {
                emit(emptyList<Expense>())
            }
            coEvery { findAllExpensesUseCase.invoke() } returns mockFlow

            assertTrue(viewModel.cardInfoStateFlow.value.isEmpty())
            assertTrue(viewModel.loadingState.value)

            // When
            viewModel.run {
                fetchAllExpenses()
                advanceUntilIdle()
            }

            // Then
            val actual = viewModel.cardInfoStateFlow.value
            assertTrue(actual.isEmpty())
            assertFalse(viewModel.loadingState.value)
        }
    }

    @Test
    fun `When goToAddExpensePage() is called, then navigator must navigate to Add screen`() {
        // Given
        val spyNavigator = spyk(ExpenseNavigator())

        // When
        viewModel.goToAddExpensePage(spyNavigator)

        // Then
        verify(exactly = 1) { spyNavigator.navigateTo(ExpenseScreen.AddScreen) }
    }

    @Test
    fun `Given the card is expanded, When changeCardInfoExpandedState() is called, then cardInfoStateFlow must be updated with the updated state`() {
        runTest {
            // Given
            val mockExpenses = listOf(
                Expense(id = 0),
                Expense(id = 1)
            )
            val mockFlow = flow {
                emit(mockExpenses)
            }
            coEvery { findAllExpensesUseCase.invoke() } returns mockFlow

            // When
            viewModel.run {
                fetchAllExpenses()
                advanceUntilIdle()
            }
            val currentCardInfoList = viewModel.cardInfoStateFlow.value
            val targetState = currentCardInfoList.first()
            val expandedStateBefore = targetState.isExpanded
            val expandedStateAfter = !expandedStateBefore
            viewModel.changeCardInfoExpandedState(targetState)
            advanceUntilIdle()

            // Then
            val expected = mockExpenses.map {
                ExpenseCardInfoState(it)
            }.sortedByDescending { it.expense.dateTime }
            val actual = viewModel.cardInfoStateFlow.value
            actual.forEachIndexed { index, expenseCardInfoState ->
                assertEquals(expected[index].expense, expenseCardInfoState.expense)
                if (expenseCardInfoState.expense.id == targetState.expense.id) {
                    assertEquals(expandedStateAfter, expenseCardInfoState.isExpanded)
                } else {
                    assertEquals(expected[index].isExpanded, expenseCardInfoState.isExpanded)
                }
            }
            assertFalse(viewModel.loadingState.value)
        }
    }

    @Test
    fun `Given we have a target expense to be deleted, When deleteExpense() is called and operation is succeeded, then status must be Success and cardInfoStateFlow must be updated`() {
        runTest {
            // Given
            val mockExpenses = listOf(
                Expense(id = 1),
                Expense(id = 2)
            )
            val mockFlow = flow {
                emit(mockExpenses)
            }
            coEvery { findAllExpensesUseCase.invoke() } returns mockFlow
            coEvery { deleteExpenseUseCase.invoke(any()) } returns true

            // When
            viewModel.run {
                fetchAllExpenses()
                advanceUntilIdle()
            }

            val currentCardInfoList = viewModel.cardInfoStateFlow.value
            val targetState = currentCardInfoList.first()
            viewModel.deleteExpense(targetState)
            advanceUntilIdle()

            // Then
            val actualStatus = viewModel.deleteStatusState.value
            val actualList = viewModel.cardInfoStateFlow.value
            assertEquals(DeleteExpenseStatus.Success, actualStatus)
            assertNull(actualList.find { it.expense.id == targetState.expense.id })
        }
    }

    @Test
    fun `Given we have a target expense to be deleted, When deleteExpense() is called and operation is failed, then status must be Failed and cardInfoStateFlow must be updated`() {
        runTest {
            // Given
            val mockExpenses = listOf(
                Expense(id = 1),
                Expense(id = 2)
            )
            val mockFlow = flow {
                emit(mockExpenses)
            }
            coEvery { findAllExpensesUseCase.invoke() } returns mockFlow
            coEvery { deleteExpenseUseCase.invoke(any()) } returns false

            // When
            viewModel.run {
                fetchAllExpenses()
                advanceUntilIdle()
            }

            val currentCardInfoList = viewModel.cardInfoStateFlow.value
            val targetState = currentCardInfoList.first()
            viewModel.deleteExpense(targetState)
            advanceUntilIdle()

            // Then
            val actualStatus = viewModel.deleteStatusState.value
            val actualList = viewModel.cardInfoStateFlow.value
            assertEquals(DeleteExpenseStatus.Failed, actualStatus)
            assertNotNull(actualList.find { it.expense.id == targetState.expense.id })
        }
    }

    @Test
    fun `Given we use expense which it isn't in the list, When deleteExpense() is called, then status must be DataNotFound and cardInfoStateFlow must be updated`() {
        runTest {
            // Given
            val mockExpenses = listOf(
                Expense(id = 1),
                Expense(id = 2)
            )
            val mockFlow = flow {
                emit(mockExpenses)
            }
            coEvery { findAllExpensesUseCase.invoke() } returns mockFlow
            coEvery { deleteExpenseUseCase.invoke(any()) } returns false

            // When
            viewModel.run {
                fetchAllExpenses()
                advanceUntilIdle()
            }

            val targetState = ExpenseCardInfoState(Expense(id = 3))
            viewModel.deleteExpense(targetState)
            advanceUntilIdle()

            // Then
            val actualStatus = viewModel.deleteStatusState.value
            assertEquals(DeleteExpenseStatus.DataNotFoundInUi, actualStatus)
        }
    }

    @Test
    fun `When resetDeleteStatusToIdle is called, then delete status must be IDLE`() {
        viewModel.resetDeleteStatusToIdle()
        assertEquals(DeleteExpenseStatus.Idle, viewModel.deleteStatusState.value)
    }
}
