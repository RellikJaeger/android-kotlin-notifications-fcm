package com.example.android.eggtimernotifications.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.android.eggtimernotifications.App
import com.example.android.eggtimernotifications.R
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = MainActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        payloadButton.setOnClickListener { sendFcmDataPayload() }
    }

    private fun sendFcmDataPayload() {
        val payload = """
            {
                "to": "/topics/${App.fcmTopic}",
                "data": {
                    "body": "This is an FCM notification message!",
                    "title": "FCM Message"
                }
            }
        """.trim()

        val okHttpClient = OkHttpClient()

        val requestBody = payload.toRequestBody()

        val request = Request.Builder()
            .method("POST", requestBody)
            .header("Authorization", App.fcmServerKey)
            .header("Content-Type", "application/json")
            .url("https://fcm.googleapis.com/fcm/send")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, "FCM Response: $response")
            }
        })
    }
}
