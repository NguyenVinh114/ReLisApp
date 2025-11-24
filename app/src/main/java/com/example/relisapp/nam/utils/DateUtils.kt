package com.example.relisapp.nam.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    /**
     * Format String date từ database (yyyy-MM-dd HH:mm:ss)
     * sang định dạng hiển thị (dd/MM/yyyy HH:mm)
     */
    fun formatDateString(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "-"

        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            formatter.format(date ?: Date())
        } catch (e: Exception) {
            dateString // Trả về string gốc nếu parse lỗi
        }
    }

    /**
     * Lấy timestamp hiện tại theo format database
     * Dùng khi tạo user mới
     */
    fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }

    /**
     * Format Date object sang String hiển thị
     */
    fun formatDate(date: Date?): String {
        if (date == null) return "-"

        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            "-"
        }
    }

    /**
     * Parse String từ DB sang Date object
     */
    fun parseDate(dateString: String?): Date? {
        if (dateString.isNullOrBlank()) return null

        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            parser.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format date dạng ngắn: 21/11/2025
     */
    fun formatDateShort(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "-"

        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Format relative time: "2 giờ trước", "3 ngày trước"
     */
    fun getRelativeTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "-"

        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = parser.parse(dateString) ?: return dateString

            val now = Date()
            val diff = now.time - date.time

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                seconds < 60 -> "Vừa xong"
                minutes < 60 -> "$minutes phút trước"
                hours < 24 -> "$hours giờ trước"
                days < 7 -> "$days ngày trước"
                days < 30 -> "${days / 7} tuần trước"
                days < 365 -> "${days / 30} tháng trước"
                else -> "${days / 365} năm trước"
            }
        } catch (e: Exception) {
            dateString
        }
    }
}

// Extension functions cho String
fun String?.toDisplayDate(): String = DateUtils.formatDateString(this)
fun String?.toDisplayDateShort(): String = DateUtils.formatDateShort(this)
fun String?.toRelativeTime(): String = DateUtils.getRelativeTime(this)
fun String?.toDate(): Date? = DateUtils.parseDate(this)

// Extension function cho Date
fun Date?.toDisplayString(): String = DateUtils.formatDate(this)