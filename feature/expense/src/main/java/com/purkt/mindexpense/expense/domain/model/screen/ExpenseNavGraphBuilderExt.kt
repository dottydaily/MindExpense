package com.purkt.mindexpense.expense.domain.model.screen

import AddExpensePage
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.purkt.mindexpense.expense.presentation.ExpenseListPage

fun NavGraphBuilder.addExpenseListTopLevel(
    navController: NavController
) {
    navigation(
        route = ExpenseRoute.graphRoute,
        startDestination = ExpenseRoute.ExpenseListScreen.route
    ) {
        composable(ExpenseRoute.ExpenseListScreen.route) {
            ExpenseListPage(
                cardInfoList = listOf(),
                onNavigateToAddExpensePage = {
                    navController.navigate(ExpenseRoute.AddExpenseScreen.route)
                }
            )
        }
        composable(ExpenseRoute.AddExpenseScreen.route) {
            AddExpensePage(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
