package com.weather.photo.view.activity.captureImage

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weather.photo.MyApplication
import com.weather.photo.R
import com.weather.photo.cameraCore.Camera2
import com.weather.photo.cameraCore.CameraPreview
import com.weather.photo.model.responseAPIModel.WeatherResponseModel
import com.weather.photo.remoteConnection.JsonParser
import com.weather.photo.remoteConnection.URL
import com.weather.photo.remoteConnection.remoteService.RemoteCallback
import com.weather.photo.remoteConnection.remoteService.startGetMethodUsingCoroutines
import com.weather.photo.remoteConnection.setup.getDefaultParams
import com.weather.photo.view.activity.baseActivity.BaseActivityViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class CaptureImageViewModel(
    application: MyApplication
) : BaseActivityViewModel(application) {
    lateinit var observer: Observer
    var isShowLoader = MutableLiveData<Boolean>()
    var connectionErrorMessage = MutableLiveData<String>()

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


    var mLastKnownLocation: Location? = null

    init {
        isShowLoader.value = true
        connectionErrorMessage.value =
            application.context.getString(R.string.fetching_your_location)

        placeName.value = ""
        tempr.value = ""
        minTempr.value = ""
        maxTempr.value = ""
        dayOfWeek.value = ""
        time.value = ""
        date.value = ""
        weatherStatus.value = ""
    }

    fun isCamera2Initialized() = ::camera2.isInitialized
    fun ismCameraPreviewInitialized() = ::mCameraPreview.isInitialized
    fun isSavedFileInitialized() = ::savedFile.isInitialized

    fun setWeatherData(responseModel: WeatherResponseModel) {
        try {
            placeName.value =
                "${responseModel.name}, ${responseModel.sys.country}"
            tempr.value = responseModel.main.temp
            minTempr.value = responseModel.main.temp_min
            maxTempr.value = responseModel.main.temp_max
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = responseModel.dt * 1000
            val dayFormat = SimpleDateFormat("EEEE", Locale.US)
            dayOfWeek.value = dayFormat.format(calendar.time)
            var hour = calendar.get(Calendar.HOUR)
            var hourString = "$hour"
            if (hour < 10)
                hourString = "0$hour"
            var minute = calendar.get(Calendar.MINUTE)
            var minuteString = "$minute"
            if (minute < 10)
                minuteString = "0$minute"
            time.value =
                "${hourString}:${minuteString}"
            date.value =
                "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                    calendar.get(Calendar.YEAR)
                }"
            weatherStatus.value = responseModel.weather[0].main
        } catch (e: Exception) {

        }
    }

    fun getWeatherDataApi() {
        var params = getDefaultParams(application, HashMap())
        params["lat"] = mLastKnownLocation?.latitude!!
        params["lon"] = mLastKnownLocation?.longitude!!

        viewModelScope.launch {
            startGetMethodUsingCoroutines(URL.getWeatherUrl(),
                params,
                object : RemoteCallback {
                    override fun onStartConnection() {
                        isShowLoader.value = true
                        connectionErrorMessage.value =
                            application.context.getString(R.string.fetching_weather_data)
                    }

                    override fun onFailureConnection(errorMessage: Any?) {
                        try {
                            Log.i("ApiError", errorMessage.toString())
                            var responseModel =
                                JsonParser().getParentResponseModel(errorMessage.toString())
                            if (responseModel != null) {
                                connectionErrorMessage.value = responseModel.message!!
                            } else {
                                connectionErrorMessage.value =
                                    application.context.getString(R.string.something_went_wrong_please_try_again_)
                            }
                        } catch (e: Exception) {
                            connectionErrorMessage.value =
                                application.context.getString(R.string.something_went_wrong_please_try_again_)
                        }
                    }

                    override fun onSuccessConnection(response: Any?) {
                        try {
                            var responseModel =
                                JsonParser().getWeatherResponseModel(response.toString())
                            if (responseModel != null) {
                                isShowLoader.value = false
                                setWeatherData(responseModel)
                            } else {
                                connectionErrorMessage.value =
                                    application.context.getString(R.string.something_went_wrong_please_try_again_)
                            }
                        } catch (e: Exception) {
                            connectionErrorMessage.value =
                                application.context.getString(R.string.something_went_wrong_please_try_again_)
                        }
                    }

                    override fun onLoginAgain(errorMessage: Any?) {
                        onLoginAgain("")
                    }
                })
        }
    }

    interface Observer {
        fun onShowHideLoadingProgress(isShow: Boolean)
        fun onShowHideMessageDialog(title: String, message: String, isShow: Boolean)
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