package com.weather.photo.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import com.weather.photo.BuildConfig

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a notification payload.
        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.containsKey("custom")) {
            val notiBody = remoteMessage.data["custom"]
            if (notiBody != null && notiBody != "")
                checkNotificationType(notiBody)
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun checkNotificationType(body: String) {

    }

    fun getRandomNumber(): Int {
        val r = Random()
        val Low = 10000
        val High = 1000000000
        return (System.currentTimeMillis() % Integer.MAX_VALUE).toInt() + (r.nextInt(High - Low) + Low)
    }

    /**
     * Persist token to third-party servers.
     *
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    fun getDefaultParams(params: HashMap<String, Any?>): HashMap<String, Any?> {
        params.put("type", "teacher")
        params.put("app_id", "1")
        params.put("device_type", "1")
//        params.put("device_token", Preferences.instance.getUserToken())
        params.put("version_code", BuildConfig.VERSION_CODE.toString())

        return params
    }

    fun getUpdateDeviceTokenData() {
    }
}