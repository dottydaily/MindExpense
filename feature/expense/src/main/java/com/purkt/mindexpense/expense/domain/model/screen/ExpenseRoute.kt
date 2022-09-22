package com.purkt.mindexpense.expense.domain.model.screen

sealed class ExpenseRoute(val route: String) {
    companion object {
        const val graphRoute: String = "ExpenseNavGraph"
    }
    object ExpenseListScreen : ExpenseRoute(route = "ExpenseList")
    object AddExpenseScreen : ExpenseRoute(route = "AddExpense")
}
