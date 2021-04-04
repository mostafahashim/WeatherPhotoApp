package com.weather.photo.remoteConnection.setup

import com.weather.photo.BuildConfig
import com.weather.photo.MyApplication
import com.weather.photo.util.Preferences
import okhttp3.MultipartBody
import kotlin.collections.HashMap

fun getDefaultParams(
    application: MyApplication,
    params: HashMap<String, Any>
): MutableMap<String, Any> {
    params["notification_Token"] = Preferences.getUserToken()
    params["userId"] = Preferences.getUserID()
    params["user_id"] = Preferences.getUserID()
    params["user_type"] = Preferences.getUserType()
    params["userType"] = Preferences.getUserType()
    params["lang"] = Preferences.getApplicationLocale()
    params["version_code"] = BuildConfig.VERSION_CODE.toString()
    params["os_version"] = application.getOSVersion()
    params["mobile_model"] = application.getDeviceModel()

    params["applicationId"] = "0"
    params["android"] = true
    return params
}

fun getDefaultParams(
    application: MyApplication,
    builder: MultipartBody.Builder
): MultipartBody.Builder {
    builder.setType(MultipartBody.FORM)
    var token = Preferences.getUserToken()
    builder.addFormDataPart("notification_Token", token)
    builder.addFormDataPart("lang", Preferences.getApplicationLocale())
    builder.addFormDataPart("userId", Preferences.getUserID())
    builder.addFormDataPart("user_id", Preferences.getUserID())
    builder.addFormDataPart("user_type", Preferences.getUserType())
    builder.addFormDataPart("userType", Preferences.getUserType())
    builder.addFormDataPart("version_code", BuildConfig.VERSION_CODE.toString())
    builder.addFormDataPart("os_version", application.getOSVersion())
    builder.addFormDataPart("mobile_model", application.getDeviceModel())
    builder.addFormDataPart("applicationId", "0")
    builder.addFormDataPart("android", "true")
    return builder
}