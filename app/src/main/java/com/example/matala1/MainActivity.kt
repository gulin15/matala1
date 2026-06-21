package com.example.matala1

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.matala1.utilities.Constants
import com.example.matala1.utilities.SharedPreferencesManager
import com.example.matala1.utilities.Record
import com.example.matala1.utilities.HighScoresRepository
import android.location.Location

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_BTN_left: AppCompatImageView
    private lateinit var main_BTN_right: AppCompatImageView
    private lateinit var main_IMG_thiefs: Array<AppCompatImageView>
    private lateinit var main_IMG_obstacles: Array<Array<AppCompatImageView>>
    private lateinit var main_IMG_coins: Array<Array<AppCompatImageView>>

    // רכיבי ה-UI של השליטה (החצים)
    private lateinit var main_LAY_controls: androidx.appcompat.widget.LinearLayoutCompat
    // רכיבי ה-UI של הציון
    private lateinit var main_LBL_score: AppCompatTextView
    private lateinit var main_LBL_final_score: AppCompatTextView
    private lateinit var main_BTN_restart: AppCompatButton
    private lateinit var main_BTN_menu: AppCompatButton // כפתור חזרה לתפריט

    // EndGame screen
    private lateinit var main_LAY_game_over: View
    // to help check if num of wrongs has increased
    private var lastWrongsCount = 0

    // משתני שליטה ומהירות דינמיים לפי בחירת המשתמש בתפריט
    private var gameMode: String = "MODE_SLOW"
    private var delayMillis: Long = 1000
    // for sound effect
    private var mediaPlayer: android.media.MediaPlayer? = null

    // משתני מערכת החיישנים
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastSensorMoveTime: Long = 0
    private val SENSOR_MOVE_DELAY = 300L

    // רכיבי ה-GPS והמיקום
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double = 32.1133  // ברירת מחדל אפקה
    private var currentLongitude: Double = 34.8180 // ברירת מחדל אפקה
    private val LOCATION_PERMISSION_REQ_CODE = 100

    // timer (Handler + Runnable):
    private val handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false
    private val gameRunnable = object : Runnable {
        override fun run() {
            if (isTimerRunning) {
                gameManager.generateAndMoveObstacles()
                refreshUI()
                handler.postDelayed(this, delayMillis)
            }
        }
    }
    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // אתחול ה-SignalManager
        SignalManager.init(this)

        // אתחול מנהל ה-SharedPreferences המעודכן שלך
        SharedPreferencesManager.init(this)

        gameManager = GameManager(lifeCount = 3)

        // אתחול רכיבי החיישנים
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // אתחול רכיב המיקום ועדכון ה-GPS הנוכחי
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissionsAndFetch()

        initViews()
        setupGameMode()
        refreshUI()
    }

    private fun checkLocationPermissionsAndFetch() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQ_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQ_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // המשתמש אישר הרשאה, נשלוף מיקום עדכני
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            currentLatitude = location.latitude
                            currentLongitude = location.longitude
                        }
                    }
                }
            }
        }
    }

    private fun initViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_LBL_final_score = findViewById(R.id.main_LBL_final_score)
        main_BTN_restart = findViewById(R.id.main_BTN_restart)
        main_BTN_menu = findViewById(R.id.main_BTN_menu)

        main_LAY_controls = findViewById(R.id.main_LAY_controls)

        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )

        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)

        main_LAY_game_over = findViewById(R.id.main_LAY_game_over)

        main_BTN_left.setOnClickListener {
            if (!gameManager.isGameOver) {
                gameManager.moveThiefLeft()
                refreshUI()
            }
        }
        main_BTN_right.setOnClickListener {
            if (!gameManager.isGameOver) {
                gameManager.moveThiefRight()
                refreshUI()
            }
        }

        main_BTN_restart.setOnClickListener {
            restartGame()
        }

        main_BTN_menu.setOnClickListener {
            stopGameTimer()
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        main_IMG_thiefs = arrayOf(
            findViewById(R.id.main_IMG_thief_0),
            findViewById(R.id.main_IMG_thief_1),
            findViewById(R.id.main_IMG_thief_2),
            findViewById(R.id.main_IMG_thief_3),
            findViewById(R.id.main_IMG_thief_4)
        )

        main_IMG_obstacles = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_0_0),
                findViewById(R.id.main_IMG_obstacle_0_1),
                findViewById(R.id.main_IMG_obstacle_0_2),
                findViewById(R.id.main_IMG_obstacle_0_3),
                findViewById(R.id.main_IMG_obstacle_0_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_1_0),
                findViewById(R.id.main_IMG_obstacle_1_1),
                findViewById(R.id.main_IMG_obstacle_1_2),
                findViewById(R.id.main_IMG_obstacle_1_3),
                findViewById(R.id.main_IMG_obstacle_1_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_2_0),
                findViewById(R.id.main_IMG_obstacle_2_1),
                findViewById(R.id.main_IMG_obstacle_2_2),
                findViewById(R.id.main_IMG_obstacle_2_3),
                findViewById(R.id.main_IMG_obstacle_2_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_3_0),
                findViewById(R.id.main_IMG_obstacle_3_1),
                findViewById(R.id.main_IMG_obstacle_3_2),
                findViewById(R.id.main_IMG_obstacle_3_3),
                findViewById(R.id.main_IMG_obstacle_3_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_4_0),
                findViewById(R.id.main_IMG_obstacle_4_1),
                findViewById(R.id.main_IMG_obstacle_4_2),
                findViewById(R.id.main_IMG_obstacle_4_3),
                findViewById(R.id.main_IMG_obstacle_4_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_5_0),
                findViewById(R.id.main_IMG_obstacle_5_1),
                findViewById(R.id.main_IMG_obstacle_5_2),
                findViewById(R.id.main_IMG_obstacle_5_3),
                findViewById(R.id.main_IMG_obstacle_5_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_6_0),
                findViewById(R.id.main_IMG_obstacle_6_1),
                findViewById(R.id.main_IMG_obstacle_6_2),
                findViewById(R.id.main_IMG_obstacle_6_3),
                findViewById(R.id.main_IMG_obstacle_6_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_7_0),
                findViewById(R.id.main_IMG_obstacle_7_1),
                findViewById(R.id.main_IMG_obstacle_7_2),
                findViewById(R.id.main_IMG_obstacle_7_3),
                findViewById(R.id.main_IMG_obstacle_7_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_8_0),
                findViewById(R.id.main_IMG_obstacle_8_1),
                findViewById(R.id.main_IMG_obstacle_8_2),
                findViewById(R.id.main_IMG_obstacle_8_3),
                findViewById(R.id.main_IMG_obstacle_8_4)
            )
        )

        main_IMG_coins = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_coin_0_0),
                findViewById(R.id.main_IMG_coin_0_1),
                findViewById(R.id.main_IMG_coin_0_2),
                findViewById(R.id.main_IMG_coin_0_3),
                findViewById(R.id.main_IMG_coin_0_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_1_0),
                findViewById(R.id.main_IMG_coin_1_1),
                findViewById(R.id.main_IMG_coin_1_2),
                findViewById(R.id.main_IMG_coin_1_3),
                findViewById(R.id.main_IMG_coin_1_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_2_0),
                findViewById(R.id.main_IMG_coin_2_1),
                findViewById(R.id.main_IMG_coin_2_2),
                findViewById(R.id.main_IMG_coin_2_3),
                findViewById(R.id.main_IMG_coin_2_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_3_0),
                findViewById(R.id.main_IMG_coin_3_1),
                findViewById(R.id.main_IMG_coin_3_2),
                findViewById(R.id.main_IMG_coin_3_3),
                findViewById(R.id.main_IMG_coin_3_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_4_0),
                findViewById(R.id.main_IMG_coin_4_1),
                findViewById(R.id.main_IMG_coin_4_2),
                findViewById(R.id.main_IMG_coin_4_3),
                findViewById(R.id.main_IMG_coin_4_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_5_0),
                findViewById(R.id.main_IMG_coin_5_1),
                findViewById(R.id.main_IMG_coin_5_2),
                findViewById(R.id.main_IMG_coin_5_3),
                findViewById(R.id.main_IMG_coin_5_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_6_0),
                findViewById(R.id.main_IMG_coin_6_1),
                findViewById(R.id.main_IMG_coin_6_2),
                findViewById(R.id.main_IMG_coin_6_3),
                findViewById(R.id.main_IMG_coin_6_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_7_0),
                findViewById(R.id.main_IMG_coin_7_1),
                findViewById(R.id.main_IMG_coin_7_2),
                findViewById(R.id.main_IMG_coin_7_3),
                findViewById(R.id.main_IMG_coin_7_4)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_coin_8_0),
                findViewById(R.id.main_IMG_coin_8_1),
                findViewById(R.id.main_IMG_coin_8_2),
                findViewById(R.id.main_IMG_coin_8_3),
                findViewById(R.id.main_IMG_coin_8_4)
            )
        )
    }

    private fun setupGameMode() {
        gameMode = intent.getStringExtra("KEY_GAME_MODE") ?: "MODE_SLOW"

        when (gameMode) {
            "MODE_SLOW" -> {
                delayMillis = 1000L
                main_LAY_controls.visibility = View.VISIBLE
            }
            "MODE_FAST" -> {
                delayMillis = 500L
                main_LAY_controls.visibility = View.VISIBLE
            }
            "MODE_SENSORS" -> {
                delayMillis = 1000L
                main_LAY_controls.visibility = View.INVISIBLE
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || gameMode != "MODE_SENSORS" || gameManager.isGameOver) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val xVal = event.values[0]
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastSensorMoveTime > SENSOR_MOVE_DELAY) {
                if (xVal > 2.0f) {
                    gameManager.moveThiefLeft()
                    refreshUI()
                    lastSensorMoveTime = currentTime
                }
                else if (xVal < -2.0f) {
                    gameManager.moveThiefRight()
                    refreshUI()
                    lastSensorMoveTime = currentTime
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun refreshUI() {
        if (gameManager.wrongs > lastWrongsCount) {
            SignalManager.getInstance().toast("Crash!")
            SignalManager.getInstance().vibrate(500)

            // play sound effect:
            try {
                mediaPlayer?.release() // release former use of sound

                mediaPlayer = android.media.MediaPlayer.create(this, R.raw.police_siren)

                val startTimeMs = 1000L  // start at: after 1 second passed
                val durationMs = 1200L   // play for 2 seconds

                mediaPlayer?.seekTo(startTimeMs.toInt()) // jump to start point
                mediaPlayer?.start()

                // stop after duration passed
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    try {
                        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                            mediaPlayer?.stop()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, durationMs)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            lastWrongsCount = gameManager.wrongs
        }

        main_LBL_score.text = String.format("%03d", gameManager.score)

        if (gameManager.isGameOver) {
            stopGameTimer()

            val currentScore = gameManager.score

            // 1. יצירת אובייקט שיא עם קואורדינטות ה-GPS האמיתיות שהתקבלו
            val newRecord = Record(
                name = "Player 1",
                score = currentScore,
                date = System.currentTimeMillis(),
                latitude = currentLatitude,  // מיקום דינמי
                longitude = currentLongitude  // מיקום דינמי
            )

            // 2. יצירת מופע של ה-Repository והוספת השיא החדש לרשימה
            val repository = HighScoresRepository()
            repository.addRecord(newRecord)

            // 3. עדכון ה-UI עם הציון שהושג בסיום הריצה
            main_LBL_final_score.text = "GAME OVER\nFinal Score: ${String.format("%03d", currentScore)}"

            main_LAY_game_over.visibility = View.VISIBLE

            for (i in main_IMG_hearts.indices) {
                main_IMG_hearts[i].visibility = View.INVISIBLE
            }
            return
        }

        for (i in main_IMG_thiefs.indices) {
            if (i == gameManager.thiefIndex) {
                main_IMG_thiefs[i].visibility = View.VISIBLE
            } else {
                main_IMG_thiefs[i].visibility = View.INVISIBLE
            }
        }

        for (i in main_IMG_hearts.indices) {
            if (i < main_IMG_hearts.size - gameManager.wrongs) {
                main_IMG_hearts[i].visibility = View.VISIBLE
            } else {
                main_IMG_hearts[i].visibility = View.INVISIBLE
            }
        }

        for (row in 0 until GameManager.ROWS) {
            for (col in 0 until GameManager.COLS) {
                if (gameManager.obstaclesMatrix[row][col] == 1) {
                    main_IMG_obstacles[row][col].visibility = View.VISIBLE
                } else {
                    main_IMG_obstacles[row][col].visibility = View.INVISIBLE
                }

                if (gameManager.coinsMatrix[row][col] == 1) {
                    main_IMG_coins[row][col].visibility = View.VISIBLE
                } else {
                    main_IMG_coins[row][col].visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun restartGame() {
        gameManager.resetGame()
        lastWrongsCount = 0
        main_LAY_game_over.visibility = View.INVISIBLE
        refreshUI()
        // רענון/ניסיון שליפה מחודש של מיקום לקראת המשחק הבא
        checkLocationPermissionsAndFetch()
        startGameTimer()
    }

    private fun startGameTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            handler.postDelayed(gameRunnable, delayMillis)
        }
    }

    private fun stopGameTimer() {
        isTimerRunning = false
        handler.removeCallbacks(gameRunnable)
    }

    override fun onStart() {
        super.onStart()
        startGameTimer()

        if (gameMode == "MODE_SENSORS") {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopGameTimer()

        if (gameMode == "MODE_SENSORS") {
            sensorManager.unregisterListener(this)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}