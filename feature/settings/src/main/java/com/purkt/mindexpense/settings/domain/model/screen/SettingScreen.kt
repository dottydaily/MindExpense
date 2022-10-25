package com.purkt.mindexpense.settings.domain.model.screen

sealed class SettingScreen(val route: String) {
    object General : SettingScreen(route = "setting-general")
}
