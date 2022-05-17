package ru.kirill.weather_app.view.HistoryList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.kirill.weather_app.databinding.FragmentHistoryBinding
import ru.kirill.weather_app.viewmodel.AppState
import ru.kirill.weather_app.viewmodel.HistoryViewModel


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding
        get() {
            return _binding!!
        }
    private val adapter = HistoryFragmentAdapter()

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyFragmentRecyclerView.also {
            it.adapter = adapter
            val linearManager = LinearLayoutManager(requireContext())
            linearManager.reverseLayout = true
            it.layoutManager = linearManager
        }
        val observer = { data: AppState -> renderData(data) }
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getAll()
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.Loading -> {
                binding.historyFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.historyFragmentLoadingLayout.visibility = View.GONE
                view?.showSnackBar("Error: ${data.error}")
            }
            is AppState.Success -> {
                binding.historyFragmentLoadingLayout.visibility = View.GONE
                adapter.setData(data.weatherData)
            }
        }
    }

    companion object {
        fun newInstance() =
            HistoryFragment()
    }

    private fun View.showSnackBar(
        text: String,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).show()
    }

}