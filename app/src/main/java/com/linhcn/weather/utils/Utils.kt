package com.linhcn.weather.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@SuppressLint("SimpleDateFormat")
fun Date.getApplicableDate(): String? {
    return try {
        SimpleDateFormat(Constant.APPLICABLE_DATE_FORMAT).format(this)
    } catch (ex: ParseException) {
        null
    }
}

fun Date.getLetterDateOfWeek(): String {
    val locale = Locale.getDefault()
    val weekdayNameFormat: DateFormat = SimpleDateFormat("EEE", locale)
    if (this.isToday()) {
        return "Today" // FIXME
    }
    return weekdayNameFormat.format(this)
}

fun Date.isToday(): Boolean {
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(this) == sdf.format(Date())
}

fun Date.getName(): String {
    val sdf = SimpleDateFormat("EEE MMM dd, yyyy", Locale.getDefault())
    return sdf.format(this)
}

fun Date.getShortName(): String {
    val sdf = SimpleDateFormat("M/d", Locale.getDefault())
    return sdf.format(this)
}

fun Calendar.getListDateOfWeek(offsetWeek: Int): List<Date> {
    val indexOfWeek: Int = get(Calendar.DAY_OF_WEEK) - 1
    when {
        offsetWeek == 0 -> {
            set(Calendar.DATE, -(indexOfWeek + 1))
        }
        offsetWeek < 0 -> {
            val offsetDate = indexOfWeek + (7 * abs(offsetWeek))
            add(Calendar.DATE, -(offsetDate + 1))
        }
        offsetWeek > 0 -> {
            val offsetDate =
                (7 - indexOfWeek) + ((offsetWeek - 1) * 7)
            add(Calendar.DATE, offsetDate + 1)
        }
    }
    val listDateOfWeek = mutableListOf<Date>()
    for (i in 0..6) {
        set(Calendar.DAY_OF_WEEK, i)
        listDateOfWeek.add(time)
    }
    return listDateOfWeek.sorted().sortedBy {
        !it.isToday()
    }
}

fun main() {
    Calendar.getInstance().getListDateOfWeek(1)
}