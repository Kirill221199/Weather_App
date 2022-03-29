package ru.kirill.weather_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.kirill.weather_app.R
import ru.kirill.weather_app.viewmodel.MainViewModel

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    private fun renderData(data:Any) {
        Toast.makeText(requireContext(), "Work", Toast.LENGTH_LONG).show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val observer = object:Observer<Any> {
            override fun onChanged(data:Any){
                renderData(data)
            }}
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getWeather()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
            }
    }
}