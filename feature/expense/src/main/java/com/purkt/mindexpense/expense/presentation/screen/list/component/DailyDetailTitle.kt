package com.purkt.mindexpense.expense.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.mindexpense.expense.presentation.screen.list.state.ExpenseInfoItem
import com.purkt.model.domain.model.Expense
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.text.DecimalFormat
import java.time.LocalDate

@Composable
fun DailyDetailTitle(
    modifier: Modifier = Modifier,
    expenses: Collection<Expense>,
    dateDetail: ExpenseInfoItem.ExpenseDateDetail
) {
    val totalAmount = expenses.sumOf { it.amount }
    val totalAmountString = DecimalFormat("#,##0.00").format(totalAmount)
    val currency = expenses.firstOrNull()?.currency?.currencyCode ?: ""

    Card(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(50),
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = dateDetail.dateString,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSecondary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$currency $totalAmountString",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSecondary
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDailyDetailTitle() {
    MindExpenseTheme {
        Surface {
            DailyDetailTitle(
                expenses = listOf(Expense(amount = 20333.04)),
                dateDetail = ExpenseInfoItem.ExpenseDateDetail(
                    LocalDate.now()
                )
            )
        }
    }
}
