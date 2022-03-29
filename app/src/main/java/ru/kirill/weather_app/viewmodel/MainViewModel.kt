package ru.kirill.weather_app.viewmodel


import android.view.View
import android.widget.Button
import android.widget.RadioButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.kirill.weather_app.R
import ru.kirill.weather_app.Repository.RepositoryImpl
import ru.kirill.weather_app.Repository.Weather

class MainViewModel(
    private val liveData: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: RepositoryImpl = RepositoryImpl()
) : ViewModel() {


    fun getData(): LiveData<AppState> {
        return liveData
    }

    fun getWeather() {
        Thread {
            liveData.postValue(AppState.Loading)
            Thread.sleep(2000L)
            if ((0..3).random() >= 2) {
                val answer: Weather = repository.getWeatherFromServer()
                liveData.postValue(AppState.Success(answer))
            } else liveData.postValue(AppState.Error(Throwable()))
        }.start()
    }

}