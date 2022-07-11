package com.example.android.eggtimernotifications.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.util.Consts
import com.google.firebase.messaging.FirebaseMessaging

class LauncherActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = LauncherActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        getToken()
        subscribeTopic()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                Consts.fcmToken = it
                Log.i(TAG, "FCM Token: ${Consts.fcmToken}")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error: ${it.message}")
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(Consts.fcmTopic)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Log.e(TAG, "Error: ${it.message}")
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
