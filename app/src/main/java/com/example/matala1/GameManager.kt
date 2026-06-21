package com.example.matala1

import kotlin.random.Random

class GameManager(private val lifeCount: Int = 3) {
    companion object {
        const val COLS = 5 // מעודכן ל-5 עמודות לפי ה-XML ומטריצת ה-UI של MainActivity
        const val ROWS = 9 // מעודכן ל-9 שורות (0 עד 8) בדיוק כפי שהזכרת לי!
    }

    var wrongs: Int = 0
        private set // read only from outside, edit inside

    var thiefIndex: Int = 2 // הגנב מתחיל בעמודה האמצעית (מתוך 5 עמודות: 0, 1, 2, 3, 4)
        private set

    var score: Int = 0
        private set

    // obstacles matrix, 0 = empty, 1 = obstacle
    val obstaclesMatrix = Array(ROWS) { IntArray(COLS) { 0 } }

    // coins matrix, 0 = empty, 1 = coin -> פותר את בעיית ה-Compilation ב-MainActivity!
    val coinsMatrix = Array(ROWS) { IntArray(COLS) { 0 } }

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
        // בדיקת פגיעה (התנגשויות) בשורה התחתונה הנוכחית לפני שמזיזים ומייצרים שורה חדשה
        checkThiefCollision()

        if (isGameOver) {
            return
        }

        // move all obstacles and coins one row down
        for (row in ROWS - 1 downTo 1) {
            for (col in 0 until COLS) {
                obstaclesMatrix[row][col] = obstaclesMatrix[row - 1][col]
                coinsMatrix[row][col] = coinsMatrix[row - 1][col]
            }
        }

        // clear first row for both matrices
        for (col in 0 until COLS) {
            obstaclesMatrix[0][col] = 0
            coinsMatrix[0][col] = 0
        }

        // משתנה עזר כדי לדעת איפה שמנו מכשול, שלא נשים עליו בטעות גם מטבע
        var obstacleColPlaced = -1

        // generate obstacle in first row (random 30% chance)
        if (Random.nextFloat() < 0.3f) {
            val randomCol = Random.nextInt(COLS)
            obstaclesMatrix[0][randomCol] = 1
            obstacleColPlaced = randomCol
        }

        // generate coin in first row (random 15% chance)
        if (Random.nextFloat() < 0.15f) {
            val randomCol = Random.nextInt(COLS)
            // מייצרים מטבע רק אם העמודה שנבחרה אינה תפוסה כבר על ידי מכשול בשורה זו
            if (randomCol != obstacleColPlaced) {
                coinsMatrix[0][randomCol] = 1
            }
        }

        // update score every time a row passes safely
        score++
    }

    // check collision for obstacles and coins
    private fun checkThiefCollision() {
        val lastRow = ROWS - 1

        // 1. בדיקת התנגשות במכשול בשורה התחתונה
        if (obstaclesMatrix[lastRow][thiefIndex] == 1) {
            wrongs++
        }

        // 2. בדיקת איסוף מטבע בשורה התחתונה
        if (coinsMatrix[lastRow][thiefIndex] == 1) {
            score += 10 // כל מטבע מעניק בונוס של 10 נקודות לציון
            coinsMatrix[lastRow][thiefIndex] = 0 // מעלימים את המטבע מהלוגיקה לאחר שנאסף
        }
    }

    fun resetGame() {
        wrongs = 0
        thiefIndex = 2 // מחזיר את הגנב לעמודה האמצעית (אינדקס 2 מתוך 5 עמודות)
        score = 0

        // איפוס מוחלט של כל מטריצת המכשולים וכל מטריצת המטבעות
        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                obstaclesMatrix[row][col] = 0
                coinsMatrix[row][col] = 0
            }
        }
    }
}