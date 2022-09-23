package com.purkt.setting.presentation.navgraph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.purkt.navigation.domain.model.Screen
import com.purkt.setting.domain.model.screen.SettingScreen
import com.purkt.setting.presentation.general.SettingGeneralPage

fun NavGraphBuilder.addSettingTopLevel(
    navController: NavController
) {
    navigation(
        route = Screen.Setting.route,
        startDestination = SettingScreen.General.route
    ) {
        composable(SettingScreen.General.route) {
            SettingGeneralPage()
        }
    }
}
