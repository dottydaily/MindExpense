package com.purkt.model.domain.model

import java.time.LocalDate

data class DateRange(
    val id: Int,
    var startDate: LocalDate,
    var endDate: LocalDate
)
