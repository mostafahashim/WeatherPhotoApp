package com.weather.photo.remoteConnection.setup

import com.weather.photo.BuildConfig
import com.weather.photo.util.Preferences
import com.weather.photo.util.appId
import kotlin.collections.HashMap

inline fun getDefaultHeaders(isFormData: Boolean): MutableMap<String, String> {
    var params = HashMap<String, String>()
    if (!isFormData)
        params["Content-Type"] = "application/json"

    params["Accept"] = "application/json"
    params["Accept-Language"] = Preferences.getApplicationLocale()
    params["app-id"] = appId
    params["Authorization"] = if (!Preferences.getAPIToken()
            .isNullOrEmpty()
    ) "Bearer ${Preferences.getAPIToken()}" else ""
    return params
}
