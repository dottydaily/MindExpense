package com.purkt.navigation.presentation

sealed class MonthlyScreen(val route: String) {
    object List : MonthlyScreen(route = "monthly-list")
}