package ru.kirill.weather_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.kirill.weather_app.Repository.*

class DetailsViewModel(
    private val liveData: MutableLiveData<DetailsState> = MutableLiveData(),
    private var repositoryAdd: DetailsRepositoryAdd = DetailsRepositoryRoomImpl(),
    private var repository: DetailsRepository = DetailsRepositoryRetrofit2Impl()
) : ViewModel() {


    fun getLiveData() = liveData

    fun getWeather(city: City) {
        liveData.postValue(DetailsState.Loading)
        repository = if (isInternet()) {
            DetailsRepositoryRetrofit2Impl()
        } else {
            DetailsRepositoryRoomImpl()
        }
        repository.getWeatherDetails(city, object : Callback {
            override fun onResponse(weather: Weather) {
                liveData.postValue(DetailsState.Success(weather))
                if (isInternet()) {
                    repositoryAdd.addWeather(weather)
                }
            }

            override fun onFailure() {
                liveData.postValue(DetailsState.Error(Throwable()))
            }
        })

    }

    interface Callback {
        fun onResponse(weather: Weather)
        fun onFailure()
    }

    private fun isInternet(): Boolean {
        //!!! заглушка
        return true
    }
}
