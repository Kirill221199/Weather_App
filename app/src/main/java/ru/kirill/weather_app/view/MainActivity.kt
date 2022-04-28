package ru.kirill.weather_app.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import ru.kirill.weather_app.MyApp
import ru.kirill.weather_app.R
import ru.kirill.weather_app.view.HistoryList.HistoryFragment
import ru.kirill.weather_app.view.WeatherList.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance()).commit()
        }

        MyApp.getHistoryDao().getAll()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_source_history -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HistoryFragment.newInstance()).addToBackStack("")
                    .commit()
            }
            R.id.action_source_server -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, MainFragment.newInstance()).addToBackStack("").commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}