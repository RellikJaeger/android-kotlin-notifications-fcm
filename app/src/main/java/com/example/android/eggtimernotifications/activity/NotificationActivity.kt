package com.example.android.eggtimernotifications.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.eggtimernotifications.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    companion object {
        private val TAG = NotificationActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val toolbar: MaterialToolbar = notificationToolbar

        toolbar.setNavigationOnClickListener { onBackPressed() }

        if (intent.extras != null && intent.extras!!.get("data") != null) {
            val data = intent.extras!!.get("data")
            Toast.makeText(this, "Data: $data", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "Data: $data")
        }
    }
}
