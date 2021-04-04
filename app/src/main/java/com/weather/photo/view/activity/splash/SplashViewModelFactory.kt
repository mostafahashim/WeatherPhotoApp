package com.weather.photo.view.activity.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weather.photo.MyApplication

class SplashViewModelFactory(
    var application: MyApplication
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SplashViewModel(application) as T
    }
}