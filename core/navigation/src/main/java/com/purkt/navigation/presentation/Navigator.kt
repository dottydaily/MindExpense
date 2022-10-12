package com.purkt.navigation.presentation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

class Navigator(private val navController: NavController? = null) {
    var targetDestination: MindExpenseDestination? = null; private set

    fun navigateTo(
        destination: MindExpenseDestination,
        navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
    ) {
        targetDestination = destination
        navController?.navigate(destination.route, navOptionsBuilder)
    }
    fun popTo(destination: MindExpenseDestination, inclusive: Boolean = false, saveState: Boolean = false) {
        targetDestination = destination
        navController?.popBackStack(destination.route, inclusive, saveState)
    }
}
