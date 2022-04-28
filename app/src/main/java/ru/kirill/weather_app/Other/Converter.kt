package ru.kirill.weather_app.Other

import ru.kirill.weather_app.Domain.Room.HistoryEntity
import ru.kirill.weather_app.Repository.City
import ru.kirill.weather_app.Repository.DTO.FactDTO
import ru.kirill.weather_app.Repository.DTO.WeatherDTO
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.Repository.getDefaultCity

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    val fact: FactDTO = weatherDTO.factDTO
    return (Weather(getDefaultCity(), fact.temp, fact.feelsLike,fact.pressureMm,fact.icon))
}

fun convertHistoryEntityToWeather(entityList : List<HistoryEntity>): List<Weather>{
    return entityList.map{
        Weather(City(it.city, 0.0, 0.0), it.temperature, it.feelsLike,it.pressureMm, it.icon)
    }
}
fun convertWeatherToEntity(weather: Weather): HistoryEntity{
    return HistoryEntity(0, weather.city.name, weather.temperature, weather.feelsLike,weather.pressureMM, weather.icon)
}

class Converter {
}