package com.purkt.mindexpense.expense.presentation.screen.additem.state

sealed class AddExpenseStatus {
    object Idle : AddExpenseStatus()
    object Success : AddExpenseStatus()
    object Failed : AddExpenseStatus()
}
