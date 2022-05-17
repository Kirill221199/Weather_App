package ru.kirill.weather_app.view.GoogleMaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import ru.kirill.weather_app.Other.REQUEST_CODE_LOCATION
import ru.kirill.weather_app.R
import ru.kirill.weather_app.Repository.City
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.databinding.FragmentMapsCastomBinding
import ru.kirill.weather_app.view.WeatherList.DetailsFragment
import ru.kirill.weather_app.view.WeatherList.OnItemViewClickListener
import java.util.*

class MapsFragment : Fragment(), OnItemViewClickListener {

    private lateinit var map: GoogleMap
    private val markers: ArrayList<Marker> = arrayListOf()

    private var _binding: FragmentMapsCastomBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val tyumen = LatLng(57.152200, 65.527200)
        map.addMarker(MarkerOptions().position(tyumen).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(tyumen))
        map.uiSettings.isZoomControlsEnabled = true
        map.isMyLocationEnabled = true
        map.setOnMapLongClickListener {
            addMarkerToArray(it)
            drawLine()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsCastomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        checkPermissionLocation()
        buttonListener()
    }

    private fun buttonListener() {
        binding.buttonSearch.setOnClickListener {
            it.hideKeyboard()
            val searchText = binding.searchAddress.text.toString()
            // check text
            if (!isNumeric(searchText)) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val results = geocoder.getFromLocationName(searchText, 1)
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(results[0].latitude, results[0].longitude))
                        .title(searchText)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                )
                showWeather(results[0].locality.toString(),results[0].latitude, results[0].longitude)
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            results[0].latitude,
                            results[0].longitude
                        ),
                        8f
                    )
                )
            } else {
                Toast.makeText(requireContext(), "Invalid address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isNumeric(toCheck: String): Boolean {
        val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
        return toCheck.matches(regex)
    }

    private fun addMarkerToArray(location: LatLng) {
        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
        markers.add(marker)
    }

    private fun setMarker(location: LatLng, searchText: String, resourceId: Int): Marker {
        return (map.addMarker(
            MarkerOptions()
                .position(location)
                .title(searchText)
                .icon(BitmapDescriptorFactory.fromResource(resourceId))
        ))!!
    }

    private fun drawLine() {
        var previousBefore: Marker? = null
        markers.forEach { current ->
            previousBefore?.let { previous ->
                map.addPolyline(
                    PolylineOptions()
                        .add(previous.position, current.position)
                        .color(Color.RED)
                        .width(5f)
                )
            }
            previousBefore = current
        }
    }

    fun checkPermissionLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            alert()
        } else {
            mRequestPermission()
        }
    }

    private fun mRequestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
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

    override fun onItemViewClick(weather: Weather) {
        requireActivity().supportFragmentManager.beginTransaction().add(
            R.id.container,
            DetailsFragment.newInstance(Bundle().apply {
                putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
            })
        ).addToBackStack("").commit()
    }

    private fun showWeather(address: String, latitude:Double, longitude:Double){
        binding.buttonShowWeather.setOnClickListener {
            onItemViewClick(
                Weather(
                    City(
                        address,
                        latitude,
                        longitude
                    )
                )
            )
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}