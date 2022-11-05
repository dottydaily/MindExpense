package com.purkt.mindexpense.expense.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.purkt.mindexpense.expense.presentation.screen.list.ExpenseListPage
import com.purkt.navigation.presentation.ExpenseScreen
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.navigation.presentation.Navigator

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addExpenseListTopLevel(
    navigator: Navigator
) {
    navigation(
        route = NavGraphRoute.Expense.route,
        startDestination = ExpenseScreen.ListScreen.route
    ) {
        composable(
            route = ExpenseScreen.ListScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            ExpenseListPage()
        }
    }
}
