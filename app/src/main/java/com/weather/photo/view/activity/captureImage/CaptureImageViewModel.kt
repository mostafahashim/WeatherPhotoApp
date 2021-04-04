package com.weather.photo.view.activity.captureImage

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.weather.photo.MyApplication
import com.weather.photo.cameraCore.Camera2
import com.weather.photo.cameraCore.CameraPreview
import com.weather.photo.view.activity.baseActivity.BaseActivityViewModel
import java.io.File

class CaptureImageViewModel(
    application: MyApplication
) : BaseActivityViewModel(application) {
    lateinit var observer: Observer

    lateinit var camera2: Camera2
    lateinit var mCameraPreview: CameraPreview
    lateinit var capturedBitmap: Bitmap
    lateinit var savedFile: File

    var placeName = MutableLiveData<String>()
    var tempr = MutableLiveData<String>()
    var minTempr = MutableLiveData<String>()
    var maxTempr = MutableLiveData<String>()
    var dayOfWeek = MutableLiveData<String>()
    var time = MutableLiveData<String>()
    var date = MutableLiveData<String>()
    var weatherStatus = MutableLiveData<String>()

    init {
        placeName.value = ""
        tempr.value = "16"
        minTempr.value = "6"
        maxTempr.value = "20"
        dayOfWeek.value = ""
        time.value = ""
        date.value = ""
        weatherStatus.value = ""
    }

    fun isCamera2Initialized() = ::camera2.isInitialized
    fun ismCameraPreviewInitialized() = ::mCameraPreview.isInitialized
    fun isSavedFileInitialized() = ::savedFile.isInitialized

    override fun onCleared() {
        super.onCleared()
    }

    interface Observer {
        fun finishWithCancel()
        fun onSwitchCamera()
        fun onStartCapture()
        fun onFinishWithSuccess()
        fun onPrepareNewImageViews()
        fun setCameraFlashOn()
        fun setCameraFlashOff()
        fun setCameraFlashAuto()
        fun openLocationDetails()
    }

}