package com.purkt.mindexpense.expense.domain.model

sealed class DeleteExpenseStatus {
    object Idle : DeleteExpenseStatus()
    object Success : DeleteExpenseStatus()
    object Failed : DeleteExpenseStatus()
    object DataNotFoundInUi : DeleteExpenseStatus()
}
