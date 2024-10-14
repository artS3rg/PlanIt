package com.artinc.planit.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.artinc.planit.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE createdAt BETWEEN :startOfDay AND :endOfDay ORDER BY isCompleted ASC, priority DESC")
    fun getTasksByDate(startOfDay: Long, endOfDay: Long): LiveData<List<Task>>

    @Query("SELECT DISTINCT DATE(createdAt / 1000, 'unixepoch') FROM tasks ORDER BY createdAt DESC")
    fun getAllTaskDates(): LiveData<List<String>>

    @Query("SELECT createdAt FROM tasks GROUP BY createdAt ORDER BY COUNT(*) DESC LIMIT 1")
    fun getBusiestDay() : LiveData<Long>

    @Query("SELECT color, COUNT(*) AS count FROM tasks WHERE color IN ('blue', 'green', 'yellow', 'red') GROUP BY color")
    fun getTaskCountByColor(): LiveData<List<ColorCount>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getIncompleteTaskCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = 1")
    fun getFirstPriorCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = 2")
    fun getSecondPriorCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = 3")
    fun getThirdPriorCount(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)
}

data class ColorCount(
    val color: String,
    val count: Int
)