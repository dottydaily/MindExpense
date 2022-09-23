package com.purkt.mindexpense.expense.presentation.navgraph

import ExpenseAddPage
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.purkt.mindexpense.expense.domain.model.screen.ExpenseScreen
import com.purkt.mindexpense.expense.presentation.ExpenseListPage
import com.purkt.navigation.domain.model.Screen

fun NavGraphBuilder.addExpenseListTopLevel(
    navController: NavController
) {
    navigation(
        route = Screen.Expense.route,
        startDestination = ExpenseScreen.ListScreen.route
    ) {
        composable(ExpenseScreen.ListScreen.route) {
            ExpenseListPage(
                cardInfoList = listOf(),
                onNavigateToAddExpensePage = {
                    navController.navigate(ExpenseScreen.AddScreen.route)
                }
            )
        }
        composable(ExpenseScreen.AddScreen.route) {
            ExpenseAddPage(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
