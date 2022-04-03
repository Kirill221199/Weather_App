package ru.kirill.weather_app.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.kirill.weather_app.Repository.RepositoryImpl
import ru.kirill.weather_app.Repository.Weather

class MainViewModel(
    private val liveData: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: RepositoryImpl = RepositoryImpl()
) : ViewModel() {

    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(isRussian = true)
    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(isRussian = false)
    fun getWeatherFromRemoteSource() = getDataFromLocalSource(isRussian = true)

    fun getData(): LiveData<AppState> {
        return liveData
    }

    fun getDataFromLocalSource(isRussian: Boolean) {
        Thread {
            liveData.postValue(AppState.Loading)
            Thread.sleep(2000L)
            if ((0..3).random() >= 2) {
                liveData.postValue(
                    AppState.Success(
                        if (isRussian)
                            repository.getWeatherFromLocalStorageRus() else
                            repository.getWeatherFromLocalStorageWorld()
                    )
                )
            } else liveData.postValue(AppState.Error(Throwable()))
        }.start()
    }


}