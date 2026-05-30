package com.example.matala1

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

class SignalManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: SignalManager? = null

        // init SignalManager
        fun init(context: Context): SignalManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = SignalManager(context.applicationContext)
                    }
                }
            }
            return instance!!
        }

        fun getInstance(): SignalManager {
            return instance ?: throw IllegalStateException("SignalManager must be initialized")
        }
    }

    // show toast message
    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // vibrate device (used after collision)
    fun vibrate(duration: Long = 500) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}