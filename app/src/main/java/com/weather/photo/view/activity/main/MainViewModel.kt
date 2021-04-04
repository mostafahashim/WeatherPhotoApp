package com.weather.photo.view.activity.main

import com.weather.photo.MyApplication
import com.weather.photo.view.activity.baseActivity.BaseActivityViewModel

class MainViewModel(
    application: MyApplication
) : BaseActivityViewModel(application) {
    lateinit var observer: Observer

    init {
    }

    override fun onCleared() {
        super.onCleared()
    }

    interface Observer {
    }

}