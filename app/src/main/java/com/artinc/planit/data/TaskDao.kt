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
    suspend fun insert(note: Task)

    @Update
    suspend fun update(note: Task)

    @Delete
    suspend fun delete(note: Task)

    @Query("SELECT * FROM tasks ORDER BY taskPriority DESC")
    suspend fun getAllTasks(): List<Task>
}