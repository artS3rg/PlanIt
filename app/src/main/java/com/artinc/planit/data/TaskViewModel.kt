package com.artinc.planit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artinc.planit.Task
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    private val _creationDates = MutableLiveData<List<String>>()
    val creationDates: LiveData<List<String>> get() = _creationDates

    fun fetchTasks() {
        viewModelScope.launch {
            _tasks.value = repository.getAllTasks()
        }
    }

    fun fetchCreationDates() {
        viewModelScope.launch {
            _creationDates.value = repository.getDistinctCreationDates()
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insert(task)
            fetchTasks() // обновляем список задач
        }
    }
}
