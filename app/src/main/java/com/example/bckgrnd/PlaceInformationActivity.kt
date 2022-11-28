package com.example.bckgrnd

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import com.example.bckgrnd.Model.Preview
import com.example.bckgrnd.Model.WikipediaExtracts
import com.example.bckgrnd.Model.placeInfo
import com.example.bckgrnd.Remote.IPlaceApi
import com.example.bckgrnd.Remote.RetroFitPlaceClient
import com.squareup.picasso.Picasso
import retrofit2.*
import java.io.StringReader


class PlaceInformationActivity : AppCompatActivity() {
    lateinit var placeApi: IPlaceApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_information)

        val intent = intent
        val apiKey = getString(R.string.place_api_key)

        placeApi = RetroFitPlaceClient.getInstance().create(IPlaceApi::class.java)
        val infoRequest = placeApi.getInfo(intent.getStringExtra("xid") ?: "", apiKey)
        infoRequest.enqueue(object : Callback<placeInfo> {
            override fun onResponse(call: Call<placeInfo>, response: Response<placeInfo>) {
                val res = Klaxon().toJsonString(response.body()).trimIndent()
                val parsedResponse = Klaxon().parseJsonObject(StringReader(res))

                val wikipediaObject = parsedResponse.obj("wikipedia_extracts")
                val placeText = wikipediaObject?.let { Klaxon().parseFromJsonObject<WikipediaExtracts>(it) }
                val tvPlaceInfo = findViewById<TextView>(R.id.tvPlaceInfo)
                tvPlaceInfo.movementMethod = ScrollingMovementMethod()
                tvPlaceInfo.text = placeText?.text ?: "Oops, couldn't find any information about this place."

                val imageURL = parsedResponse.obj("preview")
                val a = imageURL?.let { Klaxon().parseFromJsonObject<Preview>(it) }
                val iv = findViewById<ImageView>(R.id.ivPlace)
                if (a != null) {
                    Picasso.get()
                        .load("${a.source}")
                        .resize(a.width!!.toInt(), a.height!!.toInt())
                        .into(iv)
                }
            }

            override fun onFailure(call: Call<placeInfo>, t: Throwable) {
                Toast.makeText(this@PlaceInformationActivity, "Couldn't fetch data, please try again.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}