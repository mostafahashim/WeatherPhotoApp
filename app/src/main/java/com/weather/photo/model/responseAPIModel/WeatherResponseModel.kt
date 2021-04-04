package com.weather.photo.model.responseAPIModel

import com.weather.photo.model.MainWeatherModel
import com.weather.photo.model.SystemWeatherModel
import com.weather.photo.model.WeatherModel

class WeatherResponseModel : ParentResponseModel() {
    lateinit var weather: ArrayList<WeatherModel>
    lateinit var main: MainWeatherModel
    lateinit var sys: SystemWeatherModel
    lateinit var name: String
    var dt: Long = 0

}
