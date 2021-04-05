package com.weather.photo.locationUtil

import android.app.Activity
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import com.weather.photo.R
import com.weather.photo.observer.OnAskUserAction
import com.weather.photo.util.LocationPermissionRequest
import com.weather.photo.util.showMessage

fun turnGPSOn(activity: AppCompatActivity, onAskUserAction: OnAskUserAction) {
    showMessage(
        activity,
        activity.getString(R.string.location_required),
        activity.getString(R.string.you_must_enable_device_location_to_get_data_based_on_your_location),
        object : OnAskUserAction {
            override fun onPositiveAction() {
                onAskUserAction.onPositiveAction()
            }

            override fun onNegativeAction() {
            }
        },
        false,
        activity.getString(R.string.cancel),
        activity.getString(R.string.ok),
        false
    )
}