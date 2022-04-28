package ru.kirill.weather_app

import android.app.Application
import androidx.room.Room
import ru.kirill.weather_app.Domain.Room.HistoryDao
import ru.kirill.weather_app.Domain.Room.MyDB
import java.lang.IllegalStateException

class MyApp: Application(){
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object{
        private var db: MyDB?=null
        private var appContext:MyApp?=null
        fun getHistoryDao(): HistoryDao {
            if (db == null){
                if(appContext != null){
                    db = Room.databaseBuilder(appContext!!, MyDB::class.java, "test DB").allowMainThreadQueries().build() // надо будет исправить
                }
                else{
                    throw IllegalStateException("Что-то пошло не так, пустой appContext")
                }
            }
            return db!!.historyDao()
        }
    }
}
