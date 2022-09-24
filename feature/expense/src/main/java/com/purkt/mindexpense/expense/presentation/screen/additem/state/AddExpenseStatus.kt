package com.purkt.mindexpense.expense.presentation.screen.additem.state

sealed class AddExpenseStatus {
    object Start : AddExpenseStatus()
    object Success : AddExpenseStatus()
    object Failed : AddExpenseStatus()
}