package com.example.android.eggtimernotifications.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.os.SystemClock
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.common.util.PlatformVersion

@SuppressLint("StaticFieldLeak, WrongConstant")
object FullscreenIntent {
    @JvmStatic
    private val TAG = FullscreenIntent::class.java.name

    @JvmStatic
    private lateinit var ctx: Context

    @JvmStatic
    fun init(context: Context) {
        ctx = context
    }

    @JvmStatic
    fun openApp(context: Context) {
        ctx = context
        ctx.startActivity(getAppIntent(ctx))
        Log.i(TAG, "FullscreenIntent opened ${ctx.packageName} app.")
    }

    @JvmStatic
    fun openActivity(targetClassName: Class<*>) {
        if (ctx == null) throw IllegalAccessException(
            """
                [IMPORTANT] Excepted a context.
                ---
                Example 1: FullscreenIntent.init(context) // init once
                         : FullscreenIntent.openActivity(TargetActivity::class.java)
                ---
                Example 2: FullscreenIntent.openActivity(context, TargetActivity::class.java)
                ---
            """.trimIndent()
        )
        ctx.startActivity(getActivityIntent(ctx, targetClassName))
        Log.i(TAG, "FullscreenIntent opened ${targetClassName.name} activity.")
    }

    @JvmStatic
    fun openActivity(context: Context, targetClassName: Class<*>) {
        ctx = context
        ctx.startActivity(getActivityIntent(ctx, targetClassName))
        Log.i(TAG, "FullscreenIntent opened ${targetClassName.name} activity.")
    }

    @JvmStatic
    fun openActivity(context: Context, targetClassName: Class<*>, data: Map<String, String>) {
        ctx = context
        ctx.startActivity(
            getActivityIntent(ctx, targetClassName).putExtra("data", data.toString())
        )
        Log.i(TAG, "FullscreenIntent opened ${targetClassName.name} activity.")
    }

    @JvmStatic
    fun isAppForeground(context: Context): Boolean {
        val keyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return if (keyguardManager.isKeyguardLocked) {
            false
        } else {
            if (!PlatformVersion.isAtLeastLollipop()) {
                SystemClock.sleep(10L)
            }
            val pid = Process.myPid()
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = am.runningAppProcesses
            if (appProcesses != null) {
                val appProcess: Iterator<*> = appProcesses.iterator()
                while (appProcess.hasNext()) {
                    val process = appProcess.next() as ActivityManager.RunningAppProcessInfo
                    if (process.pid == pid) {
                        return process.importance == 100
                    }
                }
            }
            false
        }
    }

    @JvmStatic
    fun getAppIntent(context: Context): Intent {
        ctx = context.applicationContext
        val focusIntent: Intent =
            ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)!!.cloneFilter()
        focusIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK +
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        Log.i(TAG, "FullscreenIntent.getAppIntent() returned ${focusIntent}.")
        return focusIntent
    }

    @JvmStatic
    fun getActivityIntent(context: Context, targetClassName: Class<*>): Intent {
        ctx = context.applicationContext
        val intent: Intent = Intent(Intent.ACTION_DEFAULT)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK +
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
            .setPackage(ctx.packageName)
            .setClassName(ctx.packageName, targetClassName.name)
        Log.i(TAG, "FullscreenIntent.getActivityIntent() returned ${intent}.")
        return intent
    }
}
