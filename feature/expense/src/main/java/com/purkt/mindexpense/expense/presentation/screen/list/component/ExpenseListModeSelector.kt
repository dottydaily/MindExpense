package com.purkt.mindexpense.expense.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.mindexpense.expense.domain.model.ExpenseListMode
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme

@Composable
fun ExpenseListModeSelector(
    modifier: Modifier = Modifier,
    mode: ExpenseListMode,
    onSelectIndividualListMode: () -> Unit = {},
    onSelectMonthlyListMode: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .then(modifier)
    ) {
        val backgroundColorSelected = MaterialTheme.colors.primary
        val backgroundColorUnselected = Color.Transparent
        val contentColorSelected = MaterialTheme.colors.onPrimary
        val contentColorUnselected = MaterialTheme.colors.onSurface
        Button(
            onClick = {
                if (mode != ExpenseListMode.INDIVIDUAL) onSelectIndividualListMode.invoke()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (mode == ExpenseListMode.INDIVIDUAL) {
                    backgroundColorSelected
                } else {
                    backgroundColorUnselected
                },
                contentColor = if (mode == ExpenseListMode.INDIVIDUAL) {
                    contentColorSelected
                } else {
                    contentColorUnselected
                }
            ),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(text = "Common")
        }
        Button(
            onClick = {
                if (mode != ExpenseListMode.MONTHLY) onSelectMonthlyListMode.invoke()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (mode == ExpenseListMode.MONTHLY) {
                    backgroundColorSelected
                } else {
                    backgroundColorUnselected
                },
                contentColor = if (mode == ExpenseListMode.MONTHLY) {
                    contentColorSelected
                } else {
                    contentColorUnselected
                }
            ),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(text = "Monthly")
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseListModeSelectorAsIndividual() {
    MindExpenseTheme {
        Surface {
            ExpenseListModeSelector(mode = ExpenseListMode.INDIVIDUAL)
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExpenseListModeSelectorAsMonthly() {
    MindExpenseTheme {
        Surface {
            ExpenseListModeSelector(mode = ExpenseListMode.MONTHLY)
        }
    }
}
