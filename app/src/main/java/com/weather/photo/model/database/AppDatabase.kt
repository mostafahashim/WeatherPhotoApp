package com.weather.photo.model.database

import androidx.room.Database
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.weather.photo.model.GalleryModel

@Database(entities = [GalleryModel::class], version = 1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun galleryDao(): GalleryDAO
}