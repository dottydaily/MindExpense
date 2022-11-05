package com.purkt.mindexpense.monthly.presentation.screen.additem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthlyExpenseAddActivity : ComponentActivity() {
    companion object {
        const val INTENT_INTEGER_RECURRING_EXPENSE_ID: String = "INTENT_INTEGER_RECURRING_EXPENSE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val targetExpenseId = intent.getIntExtra(INTENT_INTEGER_RECURRING_EXPENSE_ID, -1)
        setContent {
            MindExpenseTheme {
                MonthlyExpenseAddPage(
                    targetExpenseId = if (targetExpenseId != -1) targetExpenseId else null,
                    onClose = { this.finish() }
                )
            }
        }
    }
}
