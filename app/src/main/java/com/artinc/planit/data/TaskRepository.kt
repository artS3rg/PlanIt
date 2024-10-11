package com.artinc.planit.data

import com.artinc.planit.Task

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    suspend fun getDistinctCreationDates() : List<String>{
        return taskDao.getDistinctCreationDates()
    }
}