package com.purkt.navigation.presentation

sealed class SettingScreen(route: String) : MindExpenseDestination(route) {
    object General : SettingScreen(route = "setting-general")
}
