package com.example.matala1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.matala1.fragments.ListFragment
import com.example.matala1.fragments.MapFragment

class ScoreActivity : AppCompatActivity() {

    private lateinit var listFragment: ListFragment
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        listFragment = supportFragmentManager.findFragmentById(R.id.score_FRAME_list) as ListFragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.score_FRAME_map) as MapFragment


        // listen for clicks
        listFragment.onRecordSelectedListener = { selectedRecord ->
            // after click:
            mapFragment.zoomToLocation(
                latitude = selectedRecord.latitude,
                longitude = selectedRecord.longitude,
                title = "${selectedRecord.name} - ${selectedRecord.score} Points"
            )
        }
    }
}