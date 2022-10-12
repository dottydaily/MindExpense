package com.purkt.mindexpense.expense.presentation.navigation

import ExpenseAddPage
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.purkt.mindexpense.expense.presentation.screen.list.ExpenseListPage
import com.purkt.navigation.presentation.ExpenseScreen
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.navigation.presentation.Navigator
import com.purkt.ui.presentation.button.ui.animation.slideLeftEnter
import com.purkt.ui.presentation.button.ui.animation.slideLeftExit
import com.purkt.ui.presentation.button.ui.animation.slideRightEnter
import com.purkt.ui.presentation.button.ui.animation.slideRightExit

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
            enterTransition = { slideLeftEnter() },
            exitTransition = { slideLeftExit() },
            popEnterTransition = {
                if (navigator.targetDestination !is NavGraphRoute) {
                    slideRightEnter()
                } else {
                    EnterTransition.None
                }
            }
        ) {
            ExpenseListPage()
        }
        composable(
            route = ExpenseScreen.AddScreen.route,
            enterTransition = { slideLeftEnter() },
            popExitTransition = {
                if (navigator.targetDestination !is NavGraphRoute) {
                    slideRightExit()
                } else {
                    ExitTransition.None
                }
            }
        ) {
            ExpenseAddPage(onClose = {})
        }
    }
}
