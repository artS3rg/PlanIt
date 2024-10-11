package com.artinc.planit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artinc.planit.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("SELECT * FROM tasks ORDER BY taskPriority DESC, taskIsCompl ASC, createdDate ASC")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT DISTINCT strftime('%Y-%m-%d', createdDate / 1000, 'unixepoch') FROM tasks")
    suspend fun getDistinctCreationDates(): List<String>
}