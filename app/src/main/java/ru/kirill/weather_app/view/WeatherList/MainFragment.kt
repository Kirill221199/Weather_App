package ru.kirill.weather_app.view.WeatherList

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main.*
import ru.kirill.weather_app.Other.KEY_SP_FLAG
import ru.kirill.weather_app.Other.SP_FLAG
import ru.kirill.weather_app.R
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.databinding.FragmentMainBinding
import ru.kirill.weather_app.viewmodel.AppState
import ru.kirill.weather_app.viewmodel.MainViewModel


class MainFragment : Fragment() {

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

    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            activity?.supportFragmentManager?.apply {
                beginTransaction().add(R.id.container, DetailsFragment.newInstance(Bundle().apply {
                    putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                }))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
        }
    })


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
        if (getWeatherDataSet()){
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }
        else{
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }
        viewModel.getData().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })
        viewModel.getDataFromLocalSource(getWeatherDataSet())

    }

    private fun setWeatherDataSet(weatherData:Boolean){
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
        }.also { isDataSetRus = !isDataSetRus
            setWeatherDataSet(isDataSetRus)}
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

    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }

    private fun View.showSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

}