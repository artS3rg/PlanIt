package com.artinc.planit

data class Task(
    val taskId: Int,
    val taskName: String,
    val taskDisc: String,
    val taskColor: Colors,
    val taskPriority: Int
)