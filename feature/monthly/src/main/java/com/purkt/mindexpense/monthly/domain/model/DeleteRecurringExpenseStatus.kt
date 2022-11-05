package com.purkt.mindexpense.monthly.domain.model

sealed class DeleteRecurringExpenseStatus {
    object Idle : DeleteRecurringExpenseStatus()
    object Success : DeleteRecurringExpenseStatus()
    object Failed : DeleteRecurringExpenseStatus()
    object DataNotFoundInUi : DeleteRecurringExpenseStatus()
}
