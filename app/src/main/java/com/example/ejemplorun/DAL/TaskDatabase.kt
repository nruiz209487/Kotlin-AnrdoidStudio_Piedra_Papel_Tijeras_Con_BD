package com.example.ejemplorun.DAL

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * CREACION BD
 */
@Database(entities = [TaskEntity::class], version = 2, exportSchema = true)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}
