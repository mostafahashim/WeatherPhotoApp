package com.weather.photo.model

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import java.io.Serializable

class SystemWeatherModel : BaseObservable(), Serializable {
    lateinit var country: String
    var sunrise: Long = 0
    var sunset: Long = 0
}