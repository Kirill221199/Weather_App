package ru.kirill.weather_app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.kirill.weather_app.R
import ru.kirill.weather_app.Repository.DetailsService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit()
        }
    }
}