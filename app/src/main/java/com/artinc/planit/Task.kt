package com.artinc.planit

import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Int, // 1, 2, 3
    val color: String, // "blue", "green", "yellow", "red"
    var isCompleted: Boolean = false,
    val createdAt: Long = getStartOfDayInMillis() // Дата создания в миллисекундах
) : Parcelable

private fun getStartOfDayInMillis(): Long {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}