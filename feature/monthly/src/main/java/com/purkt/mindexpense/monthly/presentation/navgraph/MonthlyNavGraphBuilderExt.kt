package com.purkt.mindexpense.monthly.presentation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.purkt.mindexpense.monthly.domain.model.screen.MonthlyScreen
import com.purkt.mindexpense.monthly.presentation.screen.list.MonthlyListPage
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.navigation.presentation.Navigator

fun NavGraphBuilder.addMonthlyListTopLevel(
    navigator: Navigator
) {
    navigation(
        route = NavGraphRoute.Monthly.route,
        startDestination = MonthlyScreen.List.route
    ) {
        composable(MonthlyScreen.List.route) {
            MonthlyListPage()
        }
    }
}
