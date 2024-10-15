package com.artinc.planit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import com.artinc.planit.Task

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksByDate(startOfDay: Long, endOfDay: Long): LiveData<List<Task>> {
        return taskDao.getTasksByDate(startOfDay, endOfDay).distinctUntilChanged()
    }

    fun getAllTaskDates(): LiveData<List<String>> {
        return taskDao.getAllTaskDates()
    }

    fun getBusiestDay() : LiveData<Long> {
        return taskDao.getBusiestDay()
    }

    fun getTaskCountByColor() : LiveData<List<ColorCount>> {
        return taskDao.getTaskCountByColor()
    }

    fun getCompletedTaskCount() : LiveData<Int> {
        return taskDao.getCompletedTaskCount()
    }

    fun getIncompleteTaskCount() : LiveData<Int> {
        return taskDao.getIncompleteTaskCount()
    }

    fun getFirstPriorCount() : LiveData<Int> {
        return taskDao.getFirstPriorCount()
    }

    fun getSecondPriorCount() : LiveData<Int> {
        return taskDao.getSecondPriorCount()
    }

    fun getThirdPriorCount() : LiveData<Int> {
        return taskDao.getThirdPriorCount()
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
}