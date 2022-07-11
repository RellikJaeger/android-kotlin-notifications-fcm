package com.example.android.eggtimernotifications.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.util.Consts
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

@SuppressLint("BatteryLife")
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = MainActivity::class.java.name
    }

    private val batteryOptimizationRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val list = it.data
                Log.i(TAG, "Battery optimization returned: $list")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solveBatteryOptimization()

        solveNotification()

        payloadButton.setOnClickListener { sendFcmDataPayload() }
    }

    private fun solveBatteryOptimization() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !powerManager.isIgnoringBatteryOptimizations(packageName)
        ) {
            val alertDialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle("Important notice")
                .setMessage("Allow this app to always run in the background so you can receive schedule notifications")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                    Toast.makeText(
                        this,
                        "You won't receive important schedule notifications in real-time until you allow it",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Setup") { dialog, _ ->
                    dialog.dismiss()
                    batteryOptimizationRequest.launch(
                        Intent(
                            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                            Uri.parse("package:$packageName")
                        )
                    )
                }
                .create()
            alertDialog.setCancelable(false)
            alertDialog.show()
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }

    private fun solveNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        if (!notificationManager.areNotificationsEnabled()) {
            val alertDialog: AlertDialog = AlertDialog.Builder(this)
                .setTitle("Tips")
                .setMessage("Allow notifications to display app-specific notifications")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                    Toast.makeText(
                        this,
                        "You won't see app-specific notifications until you allow it",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setPositiveButton("Setup") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("app_package", packageName)
                        intent.putExtra("app_uid", applicationInfo.uid)
                        startActivity(intent)
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.data = Uri.parse("package:$packageName")
                    } else {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                }
                .create()
            alertDialog.setCancelable(false)
            alertDialog.show()
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }

    private fun sendFcmDataPayload() {
        val payload = """
            {
                "to": "/topics/${Consts.fcmTopic}",
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
            .header("Authorization", Consts.fcmServerKey)
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
