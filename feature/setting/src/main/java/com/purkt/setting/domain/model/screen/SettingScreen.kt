package com.purkt.setting.domain.model.screen

sealed class SettingScreen(val route: String) {
    object General : SettingScreen(route = "setting-general")
}
