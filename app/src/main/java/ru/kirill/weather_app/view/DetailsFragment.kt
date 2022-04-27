package ru.kirill.weather_app.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_details.*
import ru.kirill.weather_app.Repository.OnServerResponse
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.databinding.FragmentDetailsBinding
import ru.kirill.weather_app.viewmodel.DetailsState
import ru.kirill.weather_app.viewmodel.DetailsViewModel

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner, object : Observer<DetailsState> {
            override fun onChanged(t: DetailsState) {
                renderData(t)
            }
        })
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
            viewModel.getWeather(it.city)
        }
    }


    private fun renderData(detailsState: DetailsState) {

        when (detailsState) {
            DetailsState.Loading -> {
                Log.d("@@@","loading")
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is DetailsState.Error -> {
                Log.d("@@@","error")
                with(binding) {
                    loadingLayout.visibility = View.GONE
                    cityName.text = "OOPS..."
                    view?.showActionSnackBar("ERROR", "REPEAT?", {
                        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
                            viewModel.getWeather(it.city)
                        }
                    })
                }
            }
            is DetailsState.Success -> {
                Log.d("@@@","success")
                val weather = detailsState.weatherData
                with(binding) {
                    loadingLayout.visibility = View.GONE
                    cityName.text = weather.city.name
                    temperatureValue.text = weather.temperature.toString() + "℃"
                    feelsLikeValue.text = weather.feelsLike.toString() + "℃"
                    pressureValue.text = weather.pressureMM.toString() + " mm Hg"
                    headerImage.load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
                    icon.loadSvg("https://yastatic.net/weather/i/icons/blueye/color/svg/${weather.icon}.svg")
                    Log.d("@@@", weather.icon)
                    cityCoordinates.text =
                        String.format("%.3f", weather.city.lat) + " , " + String.format(
                            "%.3f",
                            weather.city.lon
                        )
                }
                view?.showSnackBar("Success")
            }
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

    fun View.showActionSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

    fun ImageView.loadSvg(url:String){
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()
        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()
        imageLoader.enqueue(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
