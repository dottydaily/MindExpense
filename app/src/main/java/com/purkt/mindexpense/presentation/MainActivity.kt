package com.purkt.mindexpense.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.purkt.navigation.presentation.NavGraphRoute
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()

            MindExpenseTheme {
                val items = listOf(
                    NavGraphRoute.Expense,
                    NavGraphRoute.Setting
                )
                MainPage(
                    navController = navController,
                    navigationBarItems = items
                )
            }
        }
    }
}
