package com.purkt.mindexpense.monthly.presentation.navgraph

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.purkt.mindexpense.monthly.presentation.screen.list.MonthlyExpenseListPage
import com.purkt.navigation.presentation.MonthlyScreen
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.navigation.presentation.Navigator
import com.purkt.ui.presentation.button.ui.animation.slideRightEnter

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addMonthlyListTopLevel(
    navigator: Navigator
) {
    navigation(
        route = NavGraphRoute.Monthly.route,
        startDestination = MonthlyScreen.ListScreen.route
    ) {
        composable(
            route = MonthlyScreen.ListScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            MonthlyExpenseListPage()
        }
    }
}
