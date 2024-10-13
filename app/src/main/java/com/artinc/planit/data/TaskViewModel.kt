package com.artinc.planit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artinc.planit.Task
import kotlinx.coroutines.launch
import java.util.Calendar

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    fun getTasksByDate(date: Long): LiveData<List<Task>> {
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)
        return repository.getTasksByDate(startOfDay, endOfDay)
    }

    fun getAllTaskDates(): LiveData<List<String>> {
        return repository.getAllTaskDates()
    }

    private fun getStartOfDay(date: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(date: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }

    // Метод для добавления задачи в базу данных
    fun addTask(task: Task) {
        // Используем ViewModelScope для выполнения операции в фоновом потоке
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}
