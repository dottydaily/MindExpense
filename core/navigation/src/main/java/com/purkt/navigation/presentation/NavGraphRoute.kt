package com.purkt.navigation.presentation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.purkt.navigation.R

sealed class NavGraphRoute(route: String, @StringRes val resourceId: Int, val icon: ImageVector) : MindExpenseDestination(route) {
    object Expense : NavGraphRoute("expense", R.string.nav_bar_title_expense, Icons.Filled.List)
    object Setting : NavGraphRoute("setting", R.string.nav_bar_title_setting, Icons.Filled.Settings)
}
