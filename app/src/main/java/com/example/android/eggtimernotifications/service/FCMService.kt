package com.example.android.eggtimernotifications.service

import android.util.Log
import com.example.android.eggtimernotifications.activity.NotificationActivity
import com.example.android.eggtimernotifications.util.FullscreenIntent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            FullscreenIntent.openApp(this)
            FullscreenIntent.openActivity(
                this,
                NotificationActivity::class.java,
                remoteMessage.data
            )
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }
}
