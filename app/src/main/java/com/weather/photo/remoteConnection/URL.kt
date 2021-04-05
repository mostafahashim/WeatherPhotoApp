package com.weather.photo.remoteConnection

object URL {

    fun getWeatherUrl(): String {
        var url = "weather"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

}