package com.purkt.mindexpense.expense.presentation.navigation

import ExpenseAddPage
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.purkt.mindexpense.expense.presentation.screen.ExpenseScreen
import com.purkt.mindexpense.expense.presentation.screen.list.ExpenseListPage
import com.purkt.navigation.domain.model.Screen

fun NavGraphBuilder.addExpenseListTopLevel(
    navController: NavController
) {
    val navigator = ExpenseNavigator(navController)
    navigation(
        route = Screen.Expense.route,
        startDestination = ExpenseScreen.ListScreen.route
    ) {
        composable(ExpenseScreen.ListScreen.route) {
            ExpenseListPage(navigator = navigator)
        }
        composable(ExpenseScreen.AddScreen.route) {
            ExpenseAddPage(navigator = navigator)
        }
    }
}
