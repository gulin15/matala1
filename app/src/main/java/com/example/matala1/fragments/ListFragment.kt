package com.example.matala1.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matala1.R
import com.example.matala1.utilities.HighScoresRepository
import com.example.matala1.utilities.Record
import com.example.matala1.MenuActivity

class ListFragment : Fragment() {

    private lateinit var list_LST_scores: RecyclerView
    private lateinit var repository: HighScoresRepository
    var onRecordSelectedListener: ((Record) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // אינפלציה לקובץ ה-Layout של הפרגמנט
        val v = inflater.inflate(R.layout.fragment_list, container, false)

        list_LST_scores = v.findViewById(R.id.list_LST_scores)
        list_LST_scores.layoutManager = LinearLayoutManager(requireContext())

        repository = HighScoresRepository()
        val topScores = repository.getTopScores().take(10)

        // יצירת ה-Adapter והעברת הרשומות
        val adapter = RecordAdapter(topScores) { selectedRecord ->
            // בעת לחיצה על שורה, הפעלת המאזין והעברת הרשומה (לעדכון המפה)
            onRecordSelectedListener?.invoke(selectedRecord)
        }

        // חיבור ה-Adapter ל-RecyclerView
        list_LST_scores.adapter = adapter

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // תפיסת כפתור החזרה לתפריט מתוך ה-View המוכן של הפרגמנט
        val btnMenu = view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.score_BTN_menu)

        btnMenu?.setOnClickListener {
            val intent = Intent(requireContext(), MenuActivity::class.java)
            startActivity(intent)
            activity?.finish() // סגירת מסך השיאים כדי שלא יישאר ב-Backstack
        }
    }
}