package com.purkt.mindexpense.monthly.domain.model

sealed class DeleteExpenseStatus {
    object Idle : DeleteExpenseStatus()
    object Success : DeleteExpenseStatus()
    object Failed : DeleteExpenseStatus()
    object DataNotFoundInUi : DeleteExpenseStatus()
}
