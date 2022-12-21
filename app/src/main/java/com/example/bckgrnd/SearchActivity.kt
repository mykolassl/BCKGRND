package com.example.bckgrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.example.bckgrnd.AllPlaces.AllPlaces
import com.example.bckgrnd.Model.*
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val query = intent.extras!!.getString("searchQuery")

        val allPlacesJSON = AllPlaces().getPlaces()
        val parsed = Klaxon().parseJsonObject(StringReader(allPlacesJSON))
        val featuresArray = parsed
            .array<Any>("features")!!
            .obj("properties")
            .let {
                Klaxon().parseFromJsonArray<Properties>(it)
            }
        val placesXIDs = mutableListOf<String>()
        val placesNames = mutableListOf<String>()
        val placesDBIDs = mutableListOf<String>()
        featuresArray?.forEach { e ->
            if (e.name.contains(query!!)) {
                placesXIDs += e.xid
                placesNames += e.name
            }
        }

        val iApi = RetroFitClient.getInstance().create(IApi::class.java)
        val dbRequest = iApi.getPlaceInfo("name $query")
        dbRequest.enqueue(object : Callback<Array<tblLocationResponse>> {
            override fun onResponse(call: Call<Array<tblLocationResponse>>, response: Response<Array<tblLocationResponse>>) {
                val res = Klaxon().toJsonString(response.body()).trimIndent()
                val parsedResponse = Klaxon().parseArray<tblLocationResponse>(StringReader(res))
                parsedResponse?.forEach {
                    placesNames.add(it.Name!!)
                    placesDBIDs.add(it.ID!!)
                }

                displaySearchResults(placesXIDs, placesNames, placesDBIDs)
            }

            override fun onFailure(call: Call<Array<tblLocationResponse>>, t: Throwable) {
                displaySearchResults(placesXIDs, placesNames, placesDBIDs)
            }
        })
    }

    private fun displaySearchResults(xids: List<String>, names: List<String>, dbids: List<String>) {
        val rvResults = findViewById<RecyclerView>(R.id.rvSearchResults)
        val adapter = SearchResultAdapter(xids, names, dbids, this@SearchActivity)
        rvResults.adapter = adapter
        rvResults.layoutManager = LinearLayoutManager(this)
    }
}