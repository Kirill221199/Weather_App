package ru.kirill.weather_app.Repository

import ru.kirill.weather_app.MyApp
import ru.kirill.weather_app.Other.convertHistoryEntityToWeather
import ru.kirill.weather_app.Other.convertWeatherToEntity
import ru.kirill.weather_app.viewmodel.DetailsViewModel
import ru.kirill.weather_app.viewmodel.HistoryViewModel

class DetailsRepositoryRoomImpl: DetailsRepository,DetailsRepositoryAll,DetailsRepositoryAdd {

    override fun getWeatherDetails(city: City, callback: DetailsViewModel.Callback) {
        val list = convertHistoryEntityToWeather(MyApp.getHistoryDao().getHistoryForCity(city.name))
        if(list.isEmpty()){
            callback.onFailure()
        }else{
            callback.onResponse(list.last())
        }
    }

    override fun addWeather(weather: Weather) {
        MyApp.getHistoryDao().insert(convertWeatherToEntity(weather))
    }

    override fun getAllWeatherDetails(callback: HistoryViewModel.CallbackForAll) {
        callback.onResponse(convertHistoryEntityToWeather(MyApp.getHistoryDao().getAll()))
    }
}