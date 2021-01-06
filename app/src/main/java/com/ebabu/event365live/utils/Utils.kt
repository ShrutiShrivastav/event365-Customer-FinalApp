package com.ebabu.event365live.utils

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object Utils {

    fun LocalDate.toDate(): Date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())

}