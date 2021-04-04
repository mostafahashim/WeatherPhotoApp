package com.weather.photo.view.activity.captureImage

import android.graphics.Bitmap
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

    init {
    }

    fun isCamera2Initialized() = ::camera2.isInitialized
    fun ismCameraPreviewInitialized() = ::mCameraPreview.isInitialized
    fun isCapturedBitmapInitialized() = ::capturedBitmap.isInitialized
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
    }

}