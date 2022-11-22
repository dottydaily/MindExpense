package com.purkt.ui.presentation.button.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    backgroundColor: Color = MaterialTheme.colors.secondary,
    contentColor: Color = MaterialTheme.colors.onSecondary
) {
    val totalAmountString = DecimalFormat("#,##0.##").format(totalAmount)
    Card(
        modifier = Modifier
            .then(modifier),
        shape = RoundedCornerShape(
            topEndPercent = 50,
            bottomEndPercent = 50
        ),
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        val targetCurrency = Currency.getInstance(currency.ifBlank { "THB" }).currencyCode
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = "$targetCurrency $totalAmountString",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTotalAmountBox() {
    MindExpenseTheme {
        TotalAmountBox(totalAmount = 2097.0, currency = "THB")
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTotalAmountBoxBlankCurrency() {
    MindExpenseTheme {
        TotalAmountBox(totalAmount = 2097.0, currency = "")
    }
}
