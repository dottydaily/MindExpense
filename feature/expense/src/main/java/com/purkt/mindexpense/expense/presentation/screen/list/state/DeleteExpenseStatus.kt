package com.purkt.mindexpense.expense.presentation.screen.list.state

sealed class DeleteExpenseStatus {
    object Idle : DeleteExpenseStatus()
    object Success : DeleteExpenseStatus()
    object Failed : DeleteExpenseStatus()
    object DataNotFoundInUi : DeleteExpenseStatus()
}
