package ru.kirill.weather_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Thread.sleep

class MainViewModel (private val liveData:MutableLiveData<AppState> = MutableLiveData()) : ViewModel(){

    fun getData(): LiveData<AppState>{
        return liveData
    }

    fun getWeather() {
        Thread {
            liveData.postValue(AppState.Loading)
            sleep(2000L)
            if ((0..3).random()>=2)
            liveData.postValue(AppState.Success(Any()))
            else liveData.postValue(AppState.Error(Throwable()))
        }.start()
    }

}