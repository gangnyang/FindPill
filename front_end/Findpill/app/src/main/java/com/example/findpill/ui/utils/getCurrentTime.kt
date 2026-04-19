package com.example.findpill.ui.utils

import java.time.LocalTime
import java.util.Calendar

fun getCurrentTime(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..10 -> "morning"
        in 11..17 -> "noon"
        else -> "night"
    }
}