package com.artinc.planit.data

import com.artinc.planit.Task

class NoteRepository(private val noteDao: TaskDao) {
    suspend fun insert(note: Task) {
        noteDao.insert(note)
    }

    suspend fun update(note: Task) {
        noteDao.update(note)
    }

    suspend fun delete(note: Task) {
        noteDao.delete(note)
    }

    suspend fun getAllTasks(): List<Task> {
        return noteDao.getAllTasks()
    }
}