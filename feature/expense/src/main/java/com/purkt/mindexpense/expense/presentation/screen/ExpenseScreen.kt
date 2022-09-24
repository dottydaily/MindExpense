package com.purkt.mindexpense.expense.presentation.screen

sealed class ExpenseScreen(val route: String) {
    object ListScreen : ExpenseScreen(route = "expense-list")
    object AddScreen : ExpenseScreen(route = "expense-add")
}
