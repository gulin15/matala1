package com.example.matala1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_BTN_left: AppCompatImageView
    private lateinit var main_BTN_right: AppCompatImageView
    private lateinit var main_IMG_thiefs: Array<AppCompatImageView>
    private lateinit var main_IMG_obstacles: Array<Array<AppCompatImageView>>

    // רכיבי ה-UI של הציון
    private lateinit var main_LBL_score: AppCompatTextView
    private lateinit var main_LBL_final_score: AppCompatTextView
    private lateinit var main_BTN_restart: AppCompatButton

    // EndGame screen
    private lateinit var main_LAY_game_over: View
    // to help check if num of wrongs has increased
    private var lastWrongsCount = 0
    // timer (Handler + Runnable):
    private val handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false
    private val delayMillis: Long = 1000 // one second
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

        // initialize SignalManager
        SignalManager.init(this)

        gameManager = GameManager(lifeCount = 3)

        initViews()
        refreshUI()
    }

    private fun initViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_LBL_final_score = findViewById(R.id.main_LBL_final_score)
        main_BTN_restart = findViewById(R.id.main_BTN_restart)

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

        main_IMG_thiefs = arrayOf(
            findViewById(R.id.main_IMG_thief_left),
            findViewById(R.id.main_IMG_thief_center),
            findViewById(R.id.main_IMG_thief_right)
        )

        main_IMG_obstacles = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_0_0),
                findViewById(R.id.main_IMG_obstacle_0_1),
                findViewById(R.id.main_IMG_obstacle_0_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_1_0),
                findViewById(R.id.main_IMG_obstacle_1_1),
                findViewById(R.id.main_IMG_obstacle_1_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_2_0),
                findViewById(R.id.main_IMG_obstacle_2_1),
                findViewById(R.id.main_IMG_obstacle_2_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_3_0),
                findViewById(R.id.main_IMG_obstacle_3_1),
                findViewById(R.id.main_IMG_obstacle_3_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_obstacle_4_0),
                findViewById(R.id.main_IMG_obstacle_4_1),
                findViewById(R.id.main_IMG_obstacle_4_2)
            )
        )
    }

    private fun refreshUI() {
        // check if wrongs increased
        if (gameManager.wrongs > lastWrongsCount) {
            // use SignalManager for toast and vibrate
            SignalManager.getInstance().toast("Ooops! Crash!")
            SignalManager.getInstance().vibrate(500) // half a second of vibration

            lastWrongsCount = gameManager.wrongs
        }
        // update score
        main_LBL_score.text = String.format("%03d", gameManager.score)

        // check GAMEOVER?
        if (gameManager.isGameOver) {
            stopGameTimer()

            main_LBL_final_score.text = "Final Score: ${String.format("%03d", gameManager.score)}"

            // show GAME OVER in end screen
            main_LAY_game_over.visibility = View.VISIBLE

            // remove all hearts
            for (i in main_IMG_hearts.indices) {
                main_IMG_hearts[i].visibility = View.INVISIBLE
            }
            return
        }

        // update thief location
        for (i in main_IMG_thiefs.indices) {
            if (i == gameManager.thiefIndex) {
                main_IMG_thiefs[i].visibility = View.VISIBLE
            } else {
                main_IMG_thiefs[i].visibility = View.INVISIBLE
            }
        }

        // update hearts
        for (i in main_IMG_hearts.indices) {
            if (i < main_IMG_hearts.size - gameManager.wrongs) {
                main_IMG_hearts[i].visibility = View.VISIBLE
            } else {
                main_IMG_hearts[i].visibility = View.INVISIBLE
            }
        }

        // update obstacles matrix
        for (row in 0 until GameManager.ROWS) {
            for (col in 0 until GameManager.COLS) {
                if (gameManager.obstaclesMatrix[row][col] == 1) {
                    main_IMG_obstacles[row][col].visibility = View.VISIBLE
                } else {
                    main_IMG_obstacles[row][col].visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun restartGame() {
        gameManager.resetGame()
        lastWrongsCount = 0 // reset wrongs counter for toast check

        main_LAY_game_over.visibility = View.INVISIBLE // remove end screen

        refreshUI()
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
    }

    override fun onStop() {
        super.onStop()
        stopGameTimer()
    }
}