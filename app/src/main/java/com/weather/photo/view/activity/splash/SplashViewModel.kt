package com.weather.photo.view.activity.splash

import androidx.lifecycle.MutableLiveData
import com.weather.photo.MyApplication
import com.weather.photo.view.activity.baseActivity.BaseActivityViewModel
import com.weather.photo.util.Preferences
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class SplashViewModel(
    application: MyApplication
) : BaseActivityViewModel(application) {

    var compositeDisposable = CompositeDisposable()
    private var delay = 3000L
    var timerFinished = MutableLiveData<Boolean>()
    var connectionFinished = MutableLiveData<Boolean>()
    var progress = MutableLiveData<Int>()
    var isShowLoader = MutableLiveData<Boolean>()

    init {
        timerFinished.value = false
        isShowLoader.value = true
        connectionFinished.value = true
        progress.value = 0
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun startTimer() {
        compositeDisposable.add(
            Observable.intervalRange(1, delay, 0, 1, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it == delay) {
                        progress.value = 100
                        timerFinished.value = true
                    } else {
                        progress.value = ((it * 100 / delay)).toInt()
                    }
                }
        )
    }

}