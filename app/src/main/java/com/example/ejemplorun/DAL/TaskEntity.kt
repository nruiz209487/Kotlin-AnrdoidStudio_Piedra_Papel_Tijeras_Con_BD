package com.example.ejemplorun.DAL

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * creacion Tabla BD
 */
@Entity(tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var victoriasJugador1: Int = 0,
    var victoriasMaquina: Int = 0
)