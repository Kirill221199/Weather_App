package ru.kirill.weather_app.view.WeatherList

import ru.kirill.weather_app.Repository.Weather

interface OnItemViewClickListener {
    fun onItemViewClick(weather: Weather)
}