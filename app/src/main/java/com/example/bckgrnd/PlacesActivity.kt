package com.example.bckgrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.example.bckgrnd.AllPlaces.AllPlaces
import com.example.bckgrnd.Model.tblLocationResponse
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

class PlacesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)

        val longitude = intent.extras?.getDouble("longitude")
        val latitude = intent.extras?.getDouble("latitude")

        Log.i("MESSAGE", longitude.toString())

        if (longitude != null
            && latitude != null
            && longitude != 0.0
            && latitude != 0.0
        ) {
            val lonString = longitude.toString().replace(".", ",")
            val latString = latitude.toString().replace(".", ",")
            val distance = 2

            val query = "$latString $lonString $distance"

            val allPlacesJSON = AllPlaces().getPlaces()
            val parsed = Klaxon().parseJsonObject(StringReader(allPlacesJSON))

            val featuresArray = parsed
                .array<Any>("features")!!
                .obj("properties")
                .let {
                    Klaxon().parseFromJsonArray<Properties>(it)
                }

            val geometry = parsed
                .array<Any>("features")!!
                .obj("geometry")
                .let {
                    Klaxon().parseFromJsonArray<Geometry>(it)
                }

            val placesXIDs = mutableListOf<String>()
            val placesNames = mutableListOf<String>()
            val placesDBIDs = mutableListOf<String>()

            geometry!!.forEachIndexed { i, e ->
                if (2 * kotlin.math.atan2(
                        kotlin.math.sqrt(
                            kotlin.math.sin(abs(latitude - e.coordinates[1]) / 2f * 0.01745f)
                                .pow(2) +
                                    cos(latitude * 0.01745f) *
                                    cos(e.coordinates[1] * 0.01745f) *
                                    kotlin.math.sin(abs(longitude - e.coordinates[0]) / 2f * 0.01745f)
                                        .pow(2)
                        ),
                        kotlin.math.sqrt(
                            1 - (kotlin.math.sin(abs(latitude - e.coordinates[1]) / 2f * 0.01745f).pow(2) +
                                    cos(latitude * 0.01745f) * cos(e.coordinates[1] * 0.01745f) *
                                    kotlin.math.sin(abs(longitude - e.coordinates[0]) / 2f * 0.01745f)
                                        .pow(2))
                        )
                    ) * 6371 < distance)
                {
                    placesXIDs += featuresArray!![i].xid
                    placesNames += featuresArray!![i].name
                }
            }

            val iApi = RetroFitClient.getInstance().create(IApi::class.java)
            val dbRequest = iApi.getPlaceInfo("proximity $query")
            dbRequest.enqueue(object : Callback<Array<tblLocationResponse>> {
                override fun onResponse(call: Call<Array<tblLocationResponse>>, response: Response<Array<tblLocationResponse>>) {
                    val res = Klaxon().toJsonString(response.body()).trimIndent()
                    val parsedResponse = Klaxon().parseArray<tblLocationResponse>(StringReader(res))
                    parsedResponse?.forEach {
                        placesNames.add(it.Name!!)
                        placesDBIDs.add(it.ID!!)
                    }
                    displayResults(placesXIDs, placesNames, placesDBIDs)
                }

                override fun onFailure(call: Call<Array<tblLocationResponse>>, t: Throwable) {
                    displayResults(placesXIDs, placesNames, placesDBIDs)
                }
            })
        }
    }

    private fun displayResults(xids: List<String>, names: List<String>, dbids: List<String>) {
        val rvResults = findViewById<RecyclerView>(R.id.rvPlaces)
        val adapter = SearchResultAdapter(xids, names, dbids, this@PlacesActivity)
        rvResults.adapter = adapter
        rvResults.layoutManager = LinearLayoutManager(this)
    }
}