package com.weather.photo.view.activity.captureImage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weather.photo.MyApplication

class CaptureImageViewModelFactory(
    var application: MyApplication
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CaptureImageViewModel(application) as T
    }
}