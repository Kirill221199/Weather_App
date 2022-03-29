package ru.kirill.weather_app.Repository

class Weather(val city: City = getDefaultCity(), val temperature: Int = 0, val feelsLike: Int = 0)

fun getDefaultCity() = City("Tyumen", 57.1522, 65.5272)

data class City(val name: String, val lat: Double, val lon: Double)
