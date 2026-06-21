package com.example.matala1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    companion object {
        const val KEY_GAME_MODE = "KEY_GAME_MODE"
        const val MODE_SLOW = "MODE_SLOW"
        const val MODE_FAST = "MODE_FAST"
        const val MODE_SENSORS = "MODE_SENSORS"
    }

    // הגדרת ברירת המחדל למצב איטי (Slow Mode)
    private var selectedMode: String = MODE_SLOW

    private lateinit var btnSlow: Button
    private lateinit var btnFast: Button
    private lateinit var btnSensors: Button
    private lateinit var btnRecords: Button
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initViews()

        // צביעה ראשונית של כפתור ברירת המחדל (Slow Mode יהיה מסומן בכתום ישר בהתחלה)
        updateButtonVisuals()

        // מאזינים ללחיצות - מעדכנים את המשתנה ומשנים צבעים בהתאם
        btnSlow.setOnClickListener {
            selectedMode = MODE_SLOW
            updateButtonVisuals()
        }

        btnFast.setOnClickListener {
            selectedMode = MODE_FAST
            updateButtonVisuals()
        }

        btnSensors.setOnClickListener {
            selectedMode = MODE_SENSORS
            updateButtonVisuals()
        }

        // כפתור מעבר למסך שיאים ומפה (מעודכן ל-ScoreActivity)
        btnRecords.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }

        // כפתור התחלת המשחק עם העברת המצב הנבחר
        btnStart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(KEY_GAME_MODE, selectedMode)
            startActivity(intent)
            finish() // סגירת התפריט
        }
    }

    private fun initViews() {
        btnSlow = findViewById(R.id.menu_BTN_slow)
        btnFast = findViewById(R.id.menu_BTN_fast)
        btnSensors = findViewById(R.id.menu_BTN_sensors)
        btnRecords = findViewById(R.id.menu_BTN_records)
        btnStart = findViewById(R.id.menu_BTN_start)
    }

    // פונקציה שמנהלת את הפידבק הויזואלי לכפתור שנבחר
    private fun updateButtonVisuals() {
        // מחזירים את כל 3 כפתורי המצבים לצבע הרקע הרגיל שלהם (אפור כהה)
        btnSlow.setBackgroundColor(Color.parseColor("#424242"))
        btnFast.setBackgroundColor(Color.parseColor("#424242"))
        btnSensors.setBackgroundColor(Color.parseColor("#424242"))

        // צובעים בצבע כתום בולט אך ורק את המצב שנבחר כרגע
        when (selectedMode) {
            MODE_SLOW -> btnSlow.setBackgroundColor(Color.parseColor("#FF9800"))
            MODE_FAST -> btnFast.setBackgroundColor(Color.parseColor("#FF9800"))
            MODE_SENSORS -> btnSensors.setBackgroundColor(Color.parseColor("#FF9800"))
        }
    }
}