package ru.kirill.weather_app.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.kirill.weather_app.Repository.DetailsRepositoryRoomImpl
import ru.kirill.weather_app.Repository.RepositoryImpl
import ru.kirill.weather_app.Repository.Weather

class HistoryViewModel(
    private val liveData: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: DetailsRepositoryRoomImpl = DetailsRepositoryRoomImpl()
) : ViewModel() {

    fun getData(): LiveData<AppState> {
        return liveData
    }

    fun getAll(){
        repository.getAllWeatherDetails(object: CallbackForAll{
            override fun onResponse(listWeather: List<Weather>){
                liveData.postValue(AppState.Success(listWeather))
            }
            override fun onFail(){
            }
        })
    }
    interface CallbackForAll{
        fun onResponse(listWeather: List<Weather>)
        fun onFail()
    }

}