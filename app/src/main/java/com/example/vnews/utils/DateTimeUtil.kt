package com.example.vnews.utils

import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.abs

object DateTimeUtil {
    private val dateFormats = listOf(
        // "Fri, 21 Mar 2025 16:14:00 +0700"
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
        // "Fri, 21 Mar 25 16:14:00 +0700"
        DateTimeFormatter.ofPattern("EEE, dd MMM yy HH:mm:ss Z", Locale.US),
        //  "Fri, 21 Mar 2025 16:14:00 +07"
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss X", Locale.US),
        //  "Fri, 21 Mar 2025 16:58:32 +07:00"
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss XXX", Locale.US),
        //  "2025-03-21 16:14:00"
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
    )


    fun parseDateToUnix(dateStr: String): Long {
        val fixedDateStr = convertGMTtoOffset(dateStr)

        for (format in dateFormats) {
            try {
                val zonedDateTime = ZonedDateTime.parse(fixedDateStr, format)
                return zonedDateTime.toInstant().toEpochMilli()
            } catch (e: DateTimeParseException) {
                continue
            }
        }
        return -1L
    }

    fun getRelativeTime(unixTime: Long): String {
        val now = System.currentTimeMillis()
        val duration = Duration.ofMillis(abs(now - unixTime))
        val seconds = duration.seconds

        return when {
            seconds < 60 -> "just now"
            seconds < 3600 -> {
                val minutes = seconds / 60
                "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }

            seconds < 86400 -> {
                val hours = seconds / 3600
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }

            seconds < 604800 -> {
                val days = seconds / 86400
                "$days ${if (days == 1L) "day" else "days"} ago"
            }

            seconds < 2592000 -> {
                val weeks = seconds / 604800
                "$weeks ${if (weeks == 1L) "week" else "weeks"} ago"
            }

            seconds < 31536000 -> {
                val months = seconds / 2592000
                "$months ${if (months == 1L) "month" else "months"} ago"
            }

            else -> {
                val years = seconds / 31536000
                "$years ${if (years == 1L) "year" else "years"} ago"
            }
        }
    }

    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            diff < 2592000000 -> "${diff / 604800000}w ago"
            diff < 31536000000 -> "${diff / 2592000000}mo ago"
            else -> "${diff / 31536000000}y ago"
        }
    }

    // Chuyển đổi chuỗi GMT thành định dạng offset chuẩn
    // Ví dụ: "GMT+7" -> "+07:00"
    private fun convertGMTtoOffset(dateStr: String): String {
        return dateStr.replace(Regex("GMT([+-])(\\d{1,2})")) {
            val sign = it.groupValues[1] // Dấu + hoặc -
            val hour = it.groupValues[2].padStart(2, '0')
            "$sign$hour:00"
        }
    }

}
