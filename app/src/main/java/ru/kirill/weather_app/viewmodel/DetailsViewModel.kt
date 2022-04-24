package ru.kirill.weather_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.kirill.weather_app.Repository.City
import ru.kirill.weather_app.Repository.DetailsRepository
import ru.kirill.weather_app.Repository.DetailsRepositoryOkhttpImpl
import ru.kirill.weather_app.Repository.Weather

class DetailsViewModel(
    private val liveData: MutableLiveData<DetailsState> = MutableLiveData(),
    private var repository: DetailsRepository = DetailsRepositoryOkhttpImpl()
) : ViewModel() {

    fun getLiveData() = liveData

    fun getWeather(city: City) {
        liveData.postValue(DetailsState.Loading)
        repository.getWeatherDetails(city, object : Callback {
            override fun onResponse(weather: Weather) {
                liveData.postValue(DetailsState.Success(weather))
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

}
