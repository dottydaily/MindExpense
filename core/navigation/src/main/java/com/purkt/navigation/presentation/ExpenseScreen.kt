package com.purkt.navigation.presentation

sealed class ExpenseScreen(route: String) : MindExpenseDestination(route) {
    object ListScreen : ExpenseScreen(route = "expense-list")
    object AddScreen : ExpenseScreen(route = "expense-add")
}
