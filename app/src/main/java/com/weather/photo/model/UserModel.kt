package com.weather.photo.model

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import java.io.Serializable

class UserModel : BaseObservable(), Serializable {
    var id = ""
    var phone = ""
    var email = ""
    var name = ""

}