package com.purkt.database.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.purkt.model.domain.model.DateRange
import java.time.LocalDate

@Entity(tableName = "date_range")
data class DateRangeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "start_date") val startDate: LocalDate,
    @ColumnInfo(name = "end_date") val endDate: LocalDate
) {
    companion object {
        fun mapFromDomainModel(domain: DateRange): DateRangeEntity {
            return DateRangeEntity(
                id = domain.id,
                startDate = domain.startDate,
                endDate = domain.endDate
            )
        }

        fun mapToDomainModel(entity: DateRangeEntity): DateRange {
            return DateRange(
                id = entity.id,
                startDate = entity.startDate,
                endDate = entity.endDate
            )
        }
    }
}