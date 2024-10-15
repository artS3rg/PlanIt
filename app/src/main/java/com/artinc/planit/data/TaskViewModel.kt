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

    fun getTaskCountByColor(): LiveData<List<ColorCount>> {
        return repository.getTaskCountByColor()
    }

    fun getBusiestDay() : LiveData<Long> {
        return repository.getBusiestDay()
    }

    fun getCompletedTaskCount(): LiveData<Int> {
        return repository.getCompletedTaskCount()
    }

    fun getIncompleteTaskCount(): LiveData<Int> {
        return repository.getIncompleteTaskCount()
    }

    fun getFirstPriorCount(): LiveData<Int> {
        return repository.getFirstPriorCount()
    }

    fun getSecondPriorCount(): LiveData<Int> {
        return repository.getSecondPriorCount()
    }

    fun getThirdPriorCount(): LiveData<Int> {
        return repository.getThirdPriorCount()
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

    // Метод обновления задачи
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}
