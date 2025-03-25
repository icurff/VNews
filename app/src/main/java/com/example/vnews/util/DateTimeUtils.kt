package com.example.vnews.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.abs

object DateTimeUtils {
    private val dateFormats = listOf(
        // Format 1: Xử lý ngày tháng với năm 4 chữ số và múi giờ Z
        // Ví dụ: "Fri, 21 Mar 2025 16:14:00 +0700"
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
        
        // Format 2: Xử lý ngày tháng với năm 2 chữ số và múi giờ Z
        // Ví dụ: "Fri, 21 Mar 25 16:14:00 +0700"
        DateTimeFormatter.ofPattern("EEE, dd MMM yy HH:mm:ss Z", Locale.US),
        
        // Format 3: Xử lý ngày tháng với múi giờ X (không có GMT)
        // Ví dụ: "Fri, 21 Mar 2025 16:14:00 +07"
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss X", Locale.US),
        
        // Format 4: Xử lý ngày tháng với múi giờ XXX (có dấu :)
        // Ví dụ: "Fri, 21 Mar 2025 16:58:32 +07:00"
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss XXX", Locale.US),
        
        // Format 5: Xử lý định dạng ngày tháng đơn giản không có múi giờ
        // Ví dụ: "2025-03-21 16:14:00"
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())
    )

    /**
     * Chuyển đổi chuỗi GMT thành định dạng offset chuẩn
     * Ví dụ: "GMT+7" -> "+07:00"
     */
    private fun convertGMTtoOffset(dateStr: String): String {
        return dateStr.replace(Regex("GMT([+-])(\\d{1,2})")) {
            val sign = it.groupValues[1] // Dấu + hoặc -
            val hour = it.groupValues[2].padStart(2, '0') // Đảm bảo 2 chữ số
            "$sign$hour:00"
        }
    }

    fun parseDateToUnix(dateStr: String): Long {
        val fixedDateStr = convertGMTtoOffset(dateStr) // Sửa "GMT+7" thành "+07:00"

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
}
