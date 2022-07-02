package com.example.android.eggtimernotifications

import android.util.Log
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.example.android.eggtimernotifications.activity.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class App : MultiDexApplication() {

    companion object {
        private val TAG: String = MainActivity::class.java.name
        const val fcmTopic: String = "Alert"
        var fcmToken: String? = null
        const val fcmServerKey: String =
            "key=AAAA1s_CXkc:APA91bGnR2GrBNW2jHOVKs60ERhn4v2oqY2w1qlbtWGCyoFN48mLE1yuwnOKrWqms6po1fhtZ6_63P48voMPdy6rUJz-3x6AWRn-kF7DivwmJ4Plel9IzqR469ETEHWrqM67mcSHumX3"
    }

    override fun onCreate() {
        super.onCreate()
        getToken()
        subscribeTopic()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            fcmToken = task.result
            val msg = "FCM Token: $fcmToken"
            Log.i(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(fcmTopic)
            .addOnCompleteListener { task ->
                var message = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    message = getString(R.string.message_subscribe_failed)
                }
                Toast.makeText(this, "$message $fcmTopic.", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "$message $fcmTopic")
            }
    }
}
