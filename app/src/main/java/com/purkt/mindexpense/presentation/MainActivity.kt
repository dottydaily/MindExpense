package com.purkt.mindexpense.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.purkt.navigation.domain.model.Screen
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            MindExpenseTheme {
                val items = listOf(
                    Screen.Expense,
                    Screen.Setting
                )
                MainPage(
                    navController = navController,
                    navigationBarItems = items
                )
            }
        }
    }
}
