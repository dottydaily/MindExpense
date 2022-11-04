package com.purkt.database.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.purkt.model.domain.model.RecurringExpense
import java.time.LocalTime
import java.util.*

@Entity(tableName = "recurring_expense")
class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "amount") val amount: Double = 0.0,
    @ColumnInfo(name = "currency") val currency: String = "",
    @ColumnInfo(name = "day_of_month") val dayOfMonth: Int = 0,
    @ColumnInfo(name = "time") val time: LocalTime = LocalTime.now()
) {
    companion object {
        fun mapFromDomainModel(domain: RecurringExpense): RecurringExpenseEntity {
            return RecurringExpenseEntity(
                id = domain.id,
                title = domain.title,
                description = domain.description,
                amount = domain.amount,
                currency = domain.currency.currencyCode,
                dayOfMonth = domain.dayOfMonth,
                time = domain.time
            )
        }

        fun mapToDomainModel(entity: RecurringExpenseEntity): RecurringExpense {
            return RecurringExpense(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                amount = entity.amount,
                currency = Currency.getInstance(entity.currency),
                dayOfMonth = entity.dayOfMonth,
                time = entity.time
            )
        }
    }
}
