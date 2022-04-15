package ru.kirill.weather_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.kirill.weather_app.Repository.OnServerResponse
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.Repository.WeatherDTO
import ru.kirill.weather_app.Repository.WeatherLoader
import ru.kirill.weather_app.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(), OnServerResponse {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var currentCityName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
            currentCityName = it.city.name
            WeatherLoader(this@DetailsFragment).loadWeather(it.city.lat, it.city.lon)
        }
    }

    private fun renderData(weather: WeatherDTO) {
        with(binding) {
            loadingLayout.visibility = View.GONE
            cityName.text = currentCityName
            with(weather) {
                temperatureValue.text = weather.factDTO.temp.toString() + "℃"
                feelsLikeValue.text = weather.factDTO.feelsLike.toString()+ "℃"
                pressureValue.text = weather.factDTO.pressureMm.toString() + " mm Hg"
                cityCoordinates.text = String.format("%.3f", weather.infoDTO.lat)+" , "+String.format("%.3f", weather.infoDTO.lon)

            }
            view?.showSnackBar("Success")
        }
    }


    companion object {
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    fun View.showSnackBar(text: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, text, length).show()
    }

    override fun onResponse(weatherDTO: WeatherDTO) {
        renderData(weatherDTO)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
