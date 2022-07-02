package com.example.android.eggtimernotifications.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.eggtimernotifications.R

class NotificationActivity : AppCompatActivity() {

    companion object {
        private val TAG = NotificationActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        if (intent.extras != null && intent.extras!!.get("data") != null) {
            val data = intent.extras!!.get("data")
            Toast.makeText(this, "Data: $data", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "Data: $data")
        }
    }
}
