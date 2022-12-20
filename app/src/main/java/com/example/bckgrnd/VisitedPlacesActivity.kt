package com.example.bckgrnd

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import java.io.StringReader

class VisitedPlacesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited_places)

        val rvPlaces = findViewById<RecyclerView>(R.id.rvPlaces)

        val sharedPrefs = applicationContext.getSharedPreferences("visitedPlacesJson", Context.MODE_PRIVATE)
        val visitedPlacesJSON = sharedPrefs.getString("visitedPlacesJson", "")!!.trimIndent()

        if (visitedPlacesJSON.isNotEmpty()) {
            val parsedPlaces = Klaxon().parseJsonObject(StringReader(visitedPlacesJSON))
            val placesArray = parsedPlaces.array<Place>("data")!!.let { Klaxon().parseFromJsonArray<Place>(it) }

            val adapter = placesArray?.let { VisitedPlaceAdapter(it, this@VisitedPlacesActivity) }
            rvPlaces.adapter = adapter
            rvPlaces.layoutManager = LinearLayoutManager(this)
        }
    }
}