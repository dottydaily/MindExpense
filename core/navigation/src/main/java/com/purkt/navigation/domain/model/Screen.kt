package com.purkt.navigation.domain.model

import androidx.annotation.StringRes
import com.purkt.navigation.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Expense : Screen("expense", R.string.nav_bar_title_expense)
    object Setting : Screen("setting", R.string.nav_bar_title_setting)
}
