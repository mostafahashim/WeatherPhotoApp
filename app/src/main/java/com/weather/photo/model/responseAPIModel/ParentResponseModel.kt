package com.weather.photo.model.responseAPIModel

import androidx.databinding.BaseObservable

open class ParentResponseModel : BaseObservable() {
    lateinit var message: String
    var cod = 0
}