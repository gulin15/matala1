package com.example.matala1.utilities

data class Record(
    val name: String = "Player",
    val score: Int = 0,
    val date: Long = System.currentTimeMillis(), // תאריך בזמן ריצה
    val latitude: Double = 0.0,                  // מיקום גיאוגרפי X
    val longitude: Double = 0.0                  // מיקום גיאוגרפי Y
)