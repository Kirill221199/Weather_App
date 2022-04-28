package ru.kirill.weather_app.Domain.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val city:String,
    val temperature:Int = 0,
    val feelsLike:Int = 0,
    val pressureMm:Int = 0,
    val icon:String = "ovc"
)
