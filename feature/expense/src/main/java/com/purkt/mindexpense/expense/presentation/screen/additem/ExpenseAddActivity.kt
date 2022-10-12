package com.purkt.mindexpense.expense.presentation.screen.additem

import ExpenseAddPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseAddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindExpenseTheme {
                ExpenseAddPage(onClose = { this.finish() })
            }
        }
    }
}
