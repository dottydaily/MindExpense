package com.purkt.mindexpense.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.purkt.commonui.presentation.button.ui.theme.MindExpenseTheme
import com.purkt.mindexpense.expense.domain.model.screen.ExpenseRoute
import com.purkt.mindexpense.expense.domain.model.screen.addExpenseListTopLevel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            MindExpenseTheme {
                NavHost(
                    navController = navController,
                    startDestination = ExpenseRoute.graphRoute
                ) {
                    addExpenseListTopLevel(navController)
                }
            }
        }
    }
}
