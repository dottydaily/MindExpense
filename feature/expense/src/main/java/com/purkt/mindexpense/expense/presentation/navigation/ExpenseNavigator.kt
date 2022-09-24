package com.purkt.mindexpense.expense.presentation.navigation

import androidx.navigation.NavController
import com.purkt.mindexpense.expense.presentation.screen.ExpenseScreen

class ExpenseNavigator(private val navController: NavController? = null) {
    fun navigateTo(destination: ExpenseScreen) {
        navController?.navigate(destination.route)
    }
    fun popTo(destination: ExpenseScreen) {
        navController?.popBackStack(destination.route, inclusive = false)
    }
}
