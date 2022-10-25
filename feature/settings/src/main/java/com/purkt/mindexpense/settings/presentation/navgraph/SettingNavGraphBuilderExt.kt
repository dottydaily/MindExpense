package com.purkt.mindexpense.settings.presentation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.purkt.mindexpense.settings.domain.model.screen.SettingScreen
import com.purkt.mindexpense.settings.presentation.general.SettingGeneralPage
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.navigation.presentation.Navigator

fun NavGraphBuilder.addSettingTopLevel(
    navigator: Navigator
) {
    navigation(
        route = NavGraphRoute.Setting.route,
        startDestination = SettingScreen.General.route
    ) {
        composable(SettingScreen.General.route) {
            SettingGeneralPage()
        }
    }
}
