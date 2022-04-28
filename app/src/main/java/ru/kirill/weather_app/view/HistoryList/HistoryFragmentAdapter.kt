package ru.kirill.weather_app.view.HistoryList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import ru.kirill.weather_app.Repository.Weather
import ru.kirill.weather_app.databinding.FragmentHistoryRecyclerItemBinding

class HistoryFragmentAdapter(private var data: List<Weather> = listOf()) :
    RecyclerView.Adapter<HistoryFragmentAdapter.MyViewHolder>() {

    fun setData(dataNew: List<Weather>) {
        this.data = dataNew
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = FragmentHistoryRecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(weather: Weather) {
            FragmentHistoryRecyclerItemBinding.bind(itemView).apply {
                cityName.text = weather.city.name
                temperature.text = weather.temperature.toString() + "℃"
                feelsLike.text = weather.feelsLike.toString() + "℃"
                pressureMm.text = weather.pressureMM.toString() + " mm Hg"
                icon.loadSvg("https://yastatic.net/weather/i/icons/blueye/color/svg/${weather.icon}.svg")
            }
        }
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

}