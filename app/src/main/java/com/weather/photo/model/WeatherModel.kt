package com.weather.photo.model

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import java.io.Serializable

class WeatherModel : BaseObservable(), Serializable {
    lateinit var id :String
    lateinit var main :String
    lateinit var description :String
    lateinit var icon :String

}