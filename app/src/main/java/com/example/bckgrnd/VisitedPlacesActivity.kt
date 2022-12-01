package com.example.bckgrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VisitedPlacesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited_places)

        val rvPlaces = findViewById<RecyclerView>(R.id.rvPlaces)

        val a = mutableListOf(VisitedPlace("AAAAA"), VisitedPlace("BBBBB"), VisitedPlace("CCCCC"))
        val adapter = VisitedPlaceAdapter(a)
        rvPlaces.adapter = adapter
        rvPlaces.layoutManager = LinearLayoutManager(this)

    }
}