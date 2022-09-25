package com.purkt.mindexpense.expense.presentation.screen.list.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.text.DecimalFormat
import java.util.Currency

@Composable
fun TotalAmountBox(
    modifier: Modifier = Modifier,
    totalAmount: Double,
    currency: String,
    backgroundColor: Color = MaterialTheme.colors.primaryVariant,
    contentColor: Color = contentColorFor(backgroundColor)
) {
    val totalAmountString = DecimalFormat("#,##0.##").format(totalAmount)
    Card(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(percent = 50),
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        val targetCurrency = Currency.getInstance(currency.ifBlank { "THB" }).currencyCode
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = "$totalAmountString $targetCurrency",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun PreviewTotalAmountBox() {
    MindExpenseTheme {
        TotalAmountBox(totalAmount = 2097.0, currency = "THB")
    }
}

@Preview
@Composable
private fun PreviewTotalAmountBoxBlankCurrency() {
    MindExpenseTheme {
        TotalAmountBox(totalAmount = 2097.0, currency = "")
    }
}