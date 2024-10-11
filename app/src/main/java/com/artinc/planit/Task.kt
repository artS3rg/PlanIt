package com.artinc.planit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val taskTitle: String,
    val taskDesc: String,
    val taskColor: Colors,
    val taskPriority: Int,
    val taskIsCompl: Boolean
)