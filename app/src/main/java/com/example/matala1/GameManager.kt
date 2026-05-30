package com.example.matala1

import kotlin.random.Random

class GameManager(private val lifeCount: Int = 3) {
    companion object {
        const val COLS = 3
        const val ROWS = 5
    }

    var wrongs: Int = 0
        private set // read only from outside, edit inside

    var thiefIndex: Int = 1 // thief starts in middle
        private set

    var score: Int = 0
        private set

    // obstacles matrix, 0 = empty, 1 = obstacle
    val obstaclesMatrix = Array(ROWS) { IntArray(COLS) { 0 } }

    val isGameOver: Boolean
        get() = wrongs >= lifeCount

    fun moveThiefLeft() {
        if (thiefIndex > 0) {
            thiefIndex--
        }
    }

    fun moveThiefRight() {
        if (thiefIndex < COLS - 1) {
            thiefIndex++
        }
    }

    fun generateAndMoveObstacles() {
        checkThiefCollision()

        if (isGameOver) {
            return
        }

        // move all obstacles on row down
        for (row in ROWS - 1 downTo 1) {
            for (col in 0 until COLS) {
                obstaclesMatrix[row][col] = obstaclesMatrix[row - 1][col]
            }
        }

        // clear first row
        for (col in 0 until COLS) {
            obstaclesMatrix[0][col] = 0
        }

        // generate obstacle in first row (random)
        if (Random.nextFloat() < 0.3f) {
            val randomCol = Random.nextInt(COLS)
            obstaclesMatrix[0][randomCol] = 1
        }

        // update score every time a row passes
        score++
    }

    // check collision
    private fun checkThiefCollision() {
        val lastRow = ROWS - 1
        if (obstaclesMatrix[lastRow][thiefIndex] == 1) {
            wrongs++
        }
    }

    fun resetGame() {
        wrongs = 0
        thiefIndex = 1
        score = 0
        // איפוס מוחלט של כל המטריצה
        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                obstaclesMatrix[row][col] = 0
            }
        }
    }
}