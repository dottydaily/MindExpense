package com.purkt.setting.presentation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.navigation.presentation.Navigator
import com.purkt.setting.domain.model.screen.SettingScreen
import com.purkt.setting.presentation.general.SettingGeneralPage

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
