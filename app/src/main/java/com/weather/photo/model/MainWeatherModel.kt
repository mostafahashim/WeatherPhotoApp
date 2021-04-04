package com.weather.photo.model

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import java.io.Serializable

class MainWeatherModel : BaseObservable(), Serializable {
    lateinit var temp :String
    lateinit var feels_like :String
    lateinit var temp_min :String
    lateinit var temp_max :String
    lateinit var pressure :String
    lateinit var humidity :String
    lateinit var sea_level :String
    lateinit var grnd_level :String
}