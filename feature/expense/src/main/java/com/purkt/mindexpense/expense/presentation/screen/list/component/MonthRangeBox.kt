package com.purkt.mindexpense.expense.presentation.screen.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purkt.ui.presentation.button.ui.theme.MindExpenseTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MonthRangeBox(
    modifier: Modifier = Modifier,
    startDate: LocalDate,
    endDate: LocalDate,
    contentColor: Color = MaterialTheme.colors.onSurface,
    isShowLeftButton: Boolean = true,
    isShowRightButton: Boolean = true,
    onClickLeftArrow: () -> Unit = {},
    onClickRightArrow: () -> Unit = {}
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val rangeText = "${dateFormatter.format(startDate)} - ${dateFormatter.format(endDate)}"

    Row(
        modifier = Modifier
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isShowLeftButton) {
            IconButton(onClick = onClickLeftArrow) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    tint = contentColor,
                    imageVector = Icons.Filled.ArrowLeft,
                    contentDescription = "Back to previous month"
                )
            }
        } else {
            Spacer(
                modifier = Modifier
                    .width(24.dp)
                    .height(48.dp)
            )
        }
        Text(
            text = rangeText,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = contentColor
        )
        if (isShowRightButton) {
            IconButton(onClick = onClickRightArrow) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    tint = contentColor,
                    imageVector = Icons.Filled.ArrowRight,
                    contentDescription = "Back to previous month"
                )
            }
        } else {
            Spacer(
                modifier = Modifier
                    .width(24.dp)
                    .height(48.dp)
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMonthRangeBox() {
    MindExpenseTheme {
        Surface {
            MonthRangeBox(
                startDate = LocalDate.of(2022, 7, 25),
                endDate = LocalDate.of(2022, 8, 24)
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMonthRangeBoxHideLeftButton() {
    MindExpenseTheme {
        Surface {
            MonthRangeBox(
                startDate = LocalDate.of(2022, 7, 25),
                endDate = LocalDate.of(2022, 8, 24),
                isShowLeftButton = false
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMonthRangeBoxHideRightButton() {
    MindExpenseTheme {
        Surface {
            MonthRangeBox(
                startDate = LocalDate.of(2022, 7, 25),
                endDate = LocalDate.of(2022, 8, 24),
                isShowRightButton = false
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMonthRangeBoxHideBothButtons() {
    MindExpenseTheme {
        Surface {
            MonthRangeBox(
                startDate = LocalDate.of(2022, 7, 25),
                endDate = LocalDate.of(2022, 8, 24),
                isShowLeftButton = false,
                isShowRightButton = false
            )
        }
    }
}
