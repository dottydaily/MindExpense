package com.purkt.mindexpense.expense.presentation.screen.additem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseAddActivity : ComponentActivity() {
    companion object {
        const val INTENT_INTEGER_EXPENSE_ID: String = "INTENT_INTEGER_EXPENSE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.apply {
            statusBarColor = resources.getColor(
                com.purkt.ui.R.color.status_bar_alt_color,
                this@ExpenseAddActivity.theme
            )
            navigationBarColor = resources.getColor(
                com.purkt.ui.R.color.status_bar_alt_color,
                this@ExpenseAddActivity.theme
            )
        }

        val targetExpenseId = intent.getIntExtra(INTENT_INTEGER_EXPENSE_ID, -1)
        setContent {
            MindExpenseTheme {
                ExpenseAddPage(
                    targetExpenseId = if (targetExpenseId != -1) targetExpenseId else null,
                    onClose = { this.finish() }
                )
            }
        }
    }
}
