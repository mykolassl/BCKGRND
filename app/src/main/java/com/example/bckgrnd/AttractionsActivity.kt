package com.example.bckgrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.example.bckgrnd.AllPlaces.AllPlaces
import java.io.StringReader

data class Properties(
    var xid: String,
    var name: String,
    var rate: Int? = null,
    var osm: String? = null,
    var wikidata: String? = null,
    var kinds: String? = null
)

class AttractionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attractions)

        val rvAttractions = findViewById<RecyclerView>(R.id.rvAttractions)

        val allPlacesJSON = AllPlaces().getPlaces()
        val parsed = Klaxon().parseJsonObject(StringReader(allPlacesJSON))
        val featuresArray = parsed
            .array<Any>("features")!!
            .obj("properties")
            .let {
                Klaxon().parseFromJsonArray<Properties>(it)
            }
        val placesArray = emptyList<Place>().toMutableList()
        featuresArray!!.forEach { e ->
            placesArray += Place(e.xid, e.name)
        }
        val adapter = placesArray.let { VisitedPlaceAdapter(it, this@AttractionsActivity) }
        rvAttractions.adapter = adapter
        rvAttractions.layoutManager = LinearLayoutManager(this)
    }
}