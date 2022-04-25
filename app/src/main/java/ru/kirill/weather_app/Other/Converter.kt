package ru.kirill.weather_app.Other

import ru.kirill.weather_app.Repository.DTO.FactDTO
import ru.kirill.weather_app.Repository.DTO.WeatherDTO
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.Repository.getDefaultCity

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    val fact: FactDTO = weatherDTO.factDTO
    return (Weather(getDefaultCity(), fact.temp, fact.feelsLike,fact.pressureMm,fact.icon))
}

class Converter {
}