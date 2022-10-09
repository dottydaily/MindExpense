package com.purkt.mindexpense.expense.presentation.navigation

import ExpenseAddPage
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.purkt.mindexpense.expense.presentation.screen.ExpenseScreen
import com.purkt.mindexpense.expense.presentation.screen.list.ExpenseListPage
import com.purkt.navigation.domain.model.Screen
import com.purkt.ui.presentation.button.ui.animation.slideLeftEnter
import com.purkt.ui.presentation.button.ui.animation.slideLeftExit
import com.purkt.ui.presentation.button.ui.animation.slideRightEnter
import com.purkt.ui.presentation.button.ui.animation.slideRightExit

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addExpenseListTopLevel(
    navController: NavController
) {
    val navigator = ExpenseNavigator(navController)
    navigation(
        route = Screen.Expense.route,
        startDestination = ExpenseScreen.ListScreen.route
    ) {
        composable(
            route = ExpenseScreen.ListScreen.route,
            enterTransition = { slideLeftEnter() },
            exitTransition = { slideLeftExit() },
            popEnterTransition = { slideRightEnter() }
        ) {
            ExpenseListPage(navigator = navigator)
        }
        composable(
            route = ExpenseScreen.AddScreen.route,
            enterTransition = { slideLeftEnter() },
            popExitTransition = { slideRightExit() }
        ) {
            ExpenseAddPage(navigator = navigator)
        }
    }
}
