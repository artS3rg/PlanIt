package com.artinc.planit.data

import androidx.lifecycle.LiveData
import com.artinc.planit.Task

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksByDate(startOfDay: Long, endOfDay: Long): LiveData<List<Task>> {
        return taskDao.getTasksByDate(startOfDay, endOfDay)
    }

    fun getAllTaskDates(): LiveData<List<String>> {
        return taskDao.getAllTaskDates()
    }

    fun getBusiestDay() : Long {
        return taskDao.getBusiestDay()
    }

    fun getTaskCountByColor() : List<ColorCount> {
        return taskDao.getTaskCountByColor()
    }

    fun getCompletedTaskCount() : Int {
        return taskDao.getCompletedTaskCount()
    }

    fun getIncompleteTaskCount() : Int {
        return taskDao.getIncompleteTaskCount()
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
}