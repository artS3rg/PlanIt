package com.artinc.planit

data class Task(
    val taskName: String,
    val taskDisc: String,
    val taskColor: Colors,
    val taskPriority: Int
)