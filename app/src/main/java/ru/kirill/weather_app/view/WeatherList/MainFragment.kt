package ru.kirill.weather_app.view.WeatherList

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main.*
import ru.kirill.weather_app.Other.KEY_SP_FLAG
import ru.kirill.weather_app.Other.REQUEST_CODE_LOCATION
import ru.kirill.weather_app.Other.SP_FLAG
import ru.kirill.weather_app.R
import ru.kirill.weather_app.Repository.City
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.databinding.FragmentMainBinding
import ru.kirill.weather_app.viewmodel.AppState
import ru.kirill.weather_app.viewmodel.MainViewModel
import java.util.*


class MainFragment : Fragment(), OnItemViewClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var isDataSetRus: Boolean = true

    override fun onDestroy() {
        _binding = null
        adapter.removeListener()
        super.onDestroy()
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val adapter = MainFragmentAdapter(this)


    override fun onItemViewClick(weather: Weather) {
        requireActivity().supportFragmentManager.beginTransaction().add(
            R.id.container,
            DetailsFragment.newInstance(Bundle().apply {
                putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
            })
        ).addToBackStack("").commit()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainFragmentRecyclerView.adapter = adapter
        binding.mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
        if (getWeatherDataSet()) {
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        } else {
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }
        viewModel.getData().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })
        viewModel.getDataFromLocalSource(getWeatherDataSet())
        weatherLocation()
    }

    private fun setWeatherDataSet(weatherData: Boolean) {
        val sp = requireContext().getSharedPreferences(SP_FLAG, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sp.edit()
        editor?.putBoolean(KEY_SP_FLAG, isDataSetRus)
        editor!!.apply()
    }

    private fun getWeatherDataSet(): Boolean {
        val sp = requireContext().getSharedPreferences(SP_FLAG, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_SP_FLAG, isDataSetRus)
    }

    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }.also {
            isDataSetRus = !isDataSetRus
            setWeatherDataSet(isDataSetRus)
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                fragment_main.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { viewModel.getDataFromLocalSource(getWeatherDataSet()) })
            }
        }
    }

    companion object {
        fun newInstance() =
            MainFragment()
    }

    private fun View.showSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }


    private fun weatherLocation() {
        binding.mainFragmentLocation.setOnClickListener {
            Log.d("@@@", "нажал")
            checkPermissionLocation()
        }
    }


    private fun mRequestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
    }

    fun checkPermissionLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            alert()
        } else {
            mRequestPermission()
        }
    }

    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("@@@", location.toString())
            getAddressByLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(requireContext(), "Geolocation lost", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            Toast.makeText(requireContext(), "Geolocation enabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocation() {
        context?.let {
            val locationManager =
                it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val provaiderGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                provaiderGPS?.let {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            100f,
                            locationListener
                        )
                }
            }
        }

    }

    fun getAddressByLocation(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        Thread {
            val addressText = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                10
            )[0].getAddressLine(0)
            requireActivity().runOnUiThread {
                showAddressDialog(addressText, location)
            }
        }.start()
    }


    private fun alert() {
        AlertDialog.Builder(requireContext())
            .setTitle("Access to location")
            .setMessage("This is necessary for the application to work correctly")
            .setPositiveButton("Grant access") { _, _ ->
                mRequestPermission()
            }
            .setNegativeButton("Not necessary") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle("You address: ")
                .setMessage(address)
                .setPositiveButton("Show weather") { _, _ ->
                    onItemViewClick(
                        Weather(
                            City(
                                address,
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }
}