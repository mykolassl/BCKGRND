package com.example.bckgrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.example.bckgrnd.AllPlaces.AllPlaces
import java.io.StringReader
import java.util.*

data class Properties(
    var xid: String,
    var name: String,
    var rate: Int? = null,
    var osm: String? = null,
    var wikidata: String? = null,
    var kinds: String? = null
)

class AttractionsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var CURRENT_SELECTION = "None"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attractions)

        // Filter logic
        val spinner = findViewById<Spinner>(R.id.spinner)
        ArrayAdapter
            .createFromResource(this, R.array.tags_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        spinner.setSelection(0)
        spinner.onItemSelectedListener = this

        // Displaying places
        updateAdapter("none")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedName = parent?.getItemAtPosition(position)

        if (selectedName != CURRENT_SELECTION) {
            CURRENT_SELECTION = selectedName.toString()
            val rvPlaces = findViewById<RecyclerView>(R.id.rvAttractions)
            rvPlaces.removeAllViewsInLayout()
            updateAdapter(selectedName.toString().lowercase(Locale.ROOT))
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i("MESSAGE", "Nothing selected")
    }

    private fun updateAdapter(tag: String) {
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
            if (tag == "none") {
                placesArray += Place(e.xid, e.name)
            } else if (e.kinds?.contains(tag) == true) {
                placesArray += Place(e.xid, e.name)
            }
        }
        val adapter = VisitedPlaceAdapter(placesArray, this@AttractionsActivity)
        rvAttractions.adapter = adapter
        rvAttractions.layoutManager = LinearLayoutManager(this)
    }
}