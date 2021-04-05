package com.weather.photo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Comparator

@Entity
data class GalleryModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var sdcardPath: String? = "",
    var itemUrI: String? = "",
    var caption: String? = "",
    var isURL: Boolean = false,
    var isDeleted: Boolean = false,
    var isSeleted: Boolean = false,
    var type: String? = "",
    var albumName: String? = "",
    var index_when_selected: Int = 0,
    var item_date_modified: Int = 0
) : Serializable
