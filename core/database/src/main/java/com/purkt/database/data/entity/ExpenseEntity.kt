package com.purkt.database.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.purkt.database.domain.model.Expense
import java.time.LocalDateTime

@Entity(tableName = "expense")
class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "amount") val amount: Double = 0.0,
    @ColumnInfo(name = "currency") val currency: String = "",
    @ColumnInfo(name = "date_time") val dateTime: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun mapFromDomainModel(domain: Expense): ExpenseEntity {
            return ExpenseEntity(
                id = domain.id,
                title = domain.title,
                amount = domain.amount,
                currency = domain.currency,
                dateTime = domain.dateTime
            )
        }

        fun mapToDomainModel(entity: ExpenseEntity): Expense {
            return Expense(
                id = entity.id,
                title = entity.title,
                amount = entity.amount,
                currency = entity.currency,
                dateTime = entity.dateTime
            )
        }
    }
}
