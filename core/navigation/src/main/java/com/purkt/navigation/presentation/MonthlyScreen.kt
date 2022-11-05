package com.purkt.navigation.presentation

sealed class MonthlyScreen(val route: String) {
    object ListScreen : MonthlyScreen(route = "monthly-list")
}
