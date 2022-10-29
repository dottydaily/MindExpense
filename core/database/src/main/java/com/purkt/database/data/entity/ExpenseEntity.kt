package com.purkt.database.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.purkt.model.domain.model.Expense
import java.time.LocalDateTime
import java.util.Currency

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "amount") val amount: Double = 0.0,
    @ColumnInfo(name = "currency") val currency: String = "",
    @ColumnInfo(name = "date_time") val dateTime: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun mapFromDomainModel(domain: Expense): ExpenseEntity {
            return ExpenseEntity(
                id = domain.id,
                title = domain.title,
                description = domain.description,
                amount = domain.amount,
                currency = domain.currency.currencyCode,
                dateTime = domain.dateTime
            )
        }

        fun mapToDomainModel(entity: ExpenseEntity): Expense {
            return Expense(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                amount = entity.amount,
                currency = Currency.getInstance(entity.currency),
                dateTime = entity.dateTime
            )
        }
    }
}
