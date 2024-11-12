package com.example.ejemplorun.DAL

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * DAO CON METDODS getAll,update,insert
 */
@Dao
interface TasksDao {
    @Query("SELECT * FROM task_entity ")
    suspend fun getAll(): TaskEntity?

    @Update
    suspend fun update(task: TaskEntity)

    @Insert
    suspend fun insert(task: TaskEntity)
}