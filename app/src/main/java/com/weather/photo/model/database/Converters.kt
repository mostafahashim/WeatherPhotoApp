package com.weather.photo.model.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.weather.photo.model.GalleryModel

class Converters {
    @TypeConverter
    fun areasModelsToJson(value: List<GalleryModel>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToAreasModels(value: String) = Gson().fromJson(value, Array<GalleryModel>::class.java).toList().toCollection(ArrayList())

    @TypeConverter
    fun areaModelToJson(value: GalleryModel?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToAreaModel(value: String) = Gson().fromJson(value, GalleryModel::class.java)
}