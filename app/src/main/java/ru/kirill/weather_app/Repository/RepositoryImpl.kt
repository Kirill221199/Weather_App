package ru.kirill.weather_app.Repository

class RepositoryImpl:Repository {

    override fun getWeatherFromServer() = Weather()

    override fun getWeatherFromLocalStorageWorld() = getWorldCities()

    override fun getWeatherFromLocalStorageRus() = getRussianCities()

}