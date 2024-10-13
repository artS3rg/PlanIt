package com.artinc.planit

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Int, // 1, 2, 3
    val color: String, // "blue", "green", "yellow", "red"
    var isCompleted: Boolean = false,
    val createdAt: Long // Дата создания в миллисекундах
) : Parcelable
