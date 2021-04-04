package com.weather.photo.remoteConnection

import com.weather.photo.model.*
import com.weather.photo.model.responseAPIModel.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonParser {
    fun getParentResponseModel(response: String?): ParentResponseModel? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<ParentResponseModel>() {

            }.type
            gson.fromJson(response, type)
        } catch (e1: Exception) {
            e1.printStackTrace()
            null
        }
    }

    fun getWeatherResponseModel(response: String?): WeatherResponseModel? {
        return try {
            val gson = Gson()
            val type = object : TypeToken<WeatherResponseModel>() {

            }.type
            gson.fromJson(response, type)
        } catch (e1: Exception) {
            e1.printStackTrace()
            null
        }
    }

}


