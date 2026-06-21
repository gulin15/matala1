package com.example.matala1.utilities

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HighScoresRepository {

    // רשימה פנימית שתחזיק את השיאים
    private var recordsList: MutableList<Record> = mutableListOf()

    init {
        loadScores()
    }

    // שליפת הרשימה העדכנית
    fun getTopScores(): List<Record> {
        return recordsList.sortedByDescending { it.score }.take(10)
    }

    // הוספת שיא חדש (ובדיקה אם הוא נכנס לטופ 10)
    fun addRecord(newRecord: Record) {
        recordsList.add(newRecord)
        // ממינים מהגבוה לנמוך ומשאירים רק את ה-10 הכי טובים
        recordsList.sortByDescending { it.score }
        if (recordsList.size > 10) {
            recordsList = recordsList.subList(0, 10)
        }
        saveScores()
    }

    // שמירת כל הרשימה כטקסט JSON לתוך ה-SharedPreferencesManager שבנינו
    private fun saveScores() {
        val gson = Gson()
        val jsonString = gson.toJson(recordsList)
        SharedPreferencesManager.getInstance().putString("HIGH_SCORES_LIST_KEY", jsonString)
    }

    // טעינת הרשימה מה-SharedPreferences בעת הפעלת המשחק
    private fun loadScores() {
        val jsonString = SharedPreferencesManager.getInstance().getString("HIGH_SCORES_LIST_KEY", "")
        if (jsonString.isNotEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Record>>() {}.type
            recordsList = gson.fromJson(jsonString, type)
        } else {
            recordsList = mutableListOf()
        }
    }
}