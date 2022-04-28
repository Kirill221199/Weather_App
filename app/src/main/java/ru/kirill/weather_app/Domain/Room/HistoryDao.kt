package ru.kirill.weather_app.Domain.Room

import androidx.room.*

@Dao
interface HistoryDao {
    @Query("INSERT INTO history_table (city,temperature,feelsLike,pressureMm,icon) VALUES(:city,:temperature,:feelsLike,:pressureMm,:icon)")
    fun nativeInsert(city: String, temperature: Int, feelsLike: Int,pressureMm:Int, icon: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: HistoryEntity)

    @Delete
    fun delete(entity: HistoryEntity)

    @Update
    fun update(entity: HistoryEntity)

    @Query("SELECT * FROM history_table")
    fun getAll():List<HistoryEntity>

    @Query("SELECT * FROM history_table WHERE city=:city")
    fun getHistoryForCity(city:String):List<HistoryEntity>
}