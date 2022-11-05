package com.purkt.mindexpense.monthly.presentation.screen.additem.state

sealed class AddRecurringExpenseStatus {
    object Idle : AddRecurringExpenseStatus()
    object Success : AddRecurringExpenseStatus()
    object Failed : AddRecurringExpenseStatus()
}
