package ru.kirill.weather_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.kirill.weather_app.databinding.FragmentMainBinding
import ru.kirill.weather_app.viewmodel.AppState
import ru.kirill.weather_app.viewmodel.MainViewModel

class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                binding.message.text = "Error"
                val snackBar:Snackbar = Snackbar.make(binding.mainView,"Error",Snackbar.LENGTH_LONG)
                snackBar.setAction("Repeat request") {
                    snackBar.dismiss()
                    val viewModel = initViewModel()
                    viewModel.getWeather()
                }
                snackBar.show()
                }

            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Success -> {
                binding.loadingLayout.visibility = View.GONE
                binding.message.text = "Success"
                Snackbar.make(binding.mainView,"Success",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun initViewModel(): MainViewModel {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = initViewModel()
        val observer = object : Observer<AppState> {
            override fun onChanged(data: AppState) {
                renderData(data)
            }
        }
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

