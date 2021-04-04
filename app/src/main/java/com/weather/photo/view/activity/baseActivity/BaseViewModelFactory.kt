package com.weather.photo.view.activity.baseActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weather.photo.MyApplication

class BaseViewModelFactory(
    var application: MyApplication
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BaseActivityViewModel(application) as T
    }
}