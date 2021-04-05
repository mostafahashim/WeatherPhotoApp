package com.weather.photo.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weather.photo.model.GalleryModel
import io.reactivex.Flowable

@Dao
interface GalleryDAO {
    @Query("SELECT * FROM GalleryModel ORDER BY id DESC")
    fun getGalleryModels(): Flowable<List<GalleryModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cityModels: GalleryModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGalleryModel(galleryModel: GalleryModel)
}