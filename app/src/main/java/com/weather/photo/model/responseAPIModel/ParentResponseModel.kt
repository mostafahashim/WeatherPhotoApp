package com.weather.photo.model.responseAPIModel

import androidx.databinding.BaseObservable

open class ParentResponseModel : BaseObservable() {
    var result: Boolean? = true
    var error_message: String? = ""
    var status: String? = ""
    var success = false
    var messages: ArrayList<String>? = ArrayList()
}