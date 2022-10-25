package com.purkt.mindexpense.monthly.domain.model.screen

sealed class MonthlyScreen(val route: String) {
    object List : MonthlyScreen(route = "monthly-list")
}