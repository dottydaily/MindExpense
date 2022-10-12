package com.purkt.navigation.presentation

import androidx.annotation.StringRes
import com.purkt.navigation.R

sealed class NavGraphRoute(route: String, @StringRes val resourceId: Int) : MindExpenseDestination(route) {
    object Expense : NavGraphRoute("expense", R.string.nav_bar_title_expense)
    object Setting : NavGraphRoute("setting", R.string.nav_bar_title_setting)
}
