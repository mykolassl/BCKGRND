package com.example.bckgrnd

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import com.example.bckgrnd.Model.*
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.IPlaceApi
import com.example.bckgrnd.Remote.RetroFitClient
import com.example.bckgrnd.Remote.RetroFitPlaceClient
import com.squareup.picasso.Picasso
import retrofit2.*
import java.io.StringReader

class PlaceInformationActivity : AppCompatActivity() {
    lateinit var placeApi: IPlaceApi
    lateinit var iApi: IApi
    private lateinit var PLACE_XID: String
    private lateinit var PLACE_NAME: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_information)

        val intent = intent
        val apiKey = getString(R.string.place_api_key)

        iApi = RetroFitClient.getInstance().create(IApi::class.java)
        val dbRequest = iApi.getPlaceInfo("name ${intent.extras?.getString("name")}")
        placeApi = RetroFitPlaceClient.getInstance().create(IPlaceApi::class.java)
        val infoRequest = placeApi.getInfo(intent.getStringExtra("xid") ?: "", apiKey)
        PLACE_XID = intent.getStringExtra("xid") ?: ""

        if (PLACE_XID != "-1") {
            infoRequest.enqueue(object : Callback<placeInfo> {
                override fun onResponse(call: Call<placeInfo>, response: Response<placeInfo>) {
                    val res = Klaxon().toJsonString(response.body()).trimIndent()
                    val parsedResponse = Klaxon().parseJsonObject(StringReader(res))

                    PLACE_NAME = parsedResponse.string("name") ?: ""

                    val wikipediaObject = parsedResponse.obj("wikipedia_extracts")
                    val placeText =
                        wikipediaObject?.let { Klaxon().parseFromJsonObject<WikipediaExtracts>(it) }
                    val tvPlaceInfo = findViewById<TextView>(R.id.tvPlaceInfo)
                    tvPlaceInfo.movementMethod = ScrollingMovementMethod()
                    tvPlaceInfo.text =
                        placeText?.text ?: "Oops, couldn't find any information about this place."

                    val imageObject = parsedResponse.obj("preview")
                    val imageURL = imageObject?.let { Klaxon().parseFromJsonObject<Preview>(it) }
                    val iv = findViewById<ImageView>(R.id.ivPlace)
                    if (imageURL != null) {
                        Picasso.get()
                            .load("${imageURL.source}")
                            .resize(imageURL.width!!.toInt(), imageURL.height!!.toInt())
                            .into(iv)
                    }
                }

                override fun onFailure(call: Call<placeInfo>, t: Throwable) {
                    Toast.makeText(
                        this@PlaceInformationActivity,
                        "Couldn't fetch data, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            // BLogai, taisyt parsinima nes gaunam json array!!!!!!!!!
            dbRequest.enqueue(object : Callback<Array<tblLocationResponse>> {
                override fun onResponse(call: Call<Array<tblLocationResponse>>, response: Response<Array<tblLocationResponse>>) {
                    val res = Klaxon().toJsonString(response.body()).trimIndent()
                    val parsedResponse = Klaxon().parseJsonObject(StringReader(res))

                    PLACE_NAME = parsedResponse.string("Name") ?: ""

                    val placeText = parsedResponse.string("Description")
                    val tvPlaceInfo = findViewById<TextView>(R.id.tvPlaceInfo)
                    tvPlaceInfo.movementMethod = ScrollingMovementMethod()
                    tvPlaceInfo.text = placeText ?: "Oops, couldn't find any information about this place."

                    val base64String = parsedResponse.array<Photo>("Photos")?.get(0)?.image
                    val iv = findViewById<ImageView>(R.id.ivPlace)
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    iv.setImageBitmap(decodedImage)
                }

                override fun onFailure(call: Call<Array<tblLocationResponse>>, t: Throwable) {
                    Toast.makeText(this@PlaceInformationActivity, "Couldn't fetch data, please try again.", Toast.LENGTH_SHORT).show()
                }
            })
        }


        val ivSetVisited = findViewById<ImageView>(R.id.ivCheckmark)
        ivSetVisited.setOnClickListener {
            val sharedPrefs = applicationContext.getSharedPreferences("visitedPlacesJson", Context.MODE_PRIVATE)

            if(sharedPrefs.getString("visitedPlacesJson", "") != "") {
                // Check if clicked place exists in the JSON string.
                // If exists remove it, else add it
                val visitedPlacesJSON = sharedPrefs.getString("visitedPlacesJson", "")!!.trimIndent()
                val parsedPlaces = Klaxon().parseJsonObject(StringReader(visitedPlacesJSON))
                val placesArray = parsedPlaces.array<Place>("data")!!.let { Klaxon().parseFromJsonArray<Place>(it) }
                    ?.toMutableList()

                var isVisited: Boolean = false
                placesArray!!.forEach { e ->
                    if (e.placeName == PLACE_NAME) {
                        isVisited = true
                    }
                }

                var updatedVisitedPlacesJSON: String = ""

                if(isVisited) {
                    val foundPlaceObj = placesArray.find { e -> e.placeName == PLACE_NAME } as Place
                    placesArray -= foundPlaceObj
                    Toast.makeText(this, "$PLACE_NAME was removed from your visited places list.", Toast.LENGTH_LONG).show()
                } else {
                    val newPlace = Place(PLACE_XID, PLACE_NAME)
                    placesArray += newPlace
                    Toast.makeText(this, "$PLACE_NAME was added to your visited places list.", Toast.LENGTH_LONG).show()
                }

                updatedVisitedPlacesJSON = """
                        {
                            "data": ${Klaxon().toJsonString(placesArray)}
                        }
                    """.trimIndent()

                sharedPrefs.edit().putString("visitedPlacesJson", updatedVisitedPlacesJSON).commit()
            } else {
                val visitedPlacesJSON =
                    """
                    {
                        "data":
                        [
                            {
                                "xid": "$PLACE_XID",
                                "placeName": "$PLACE_NAME"
                            }
                        ]
                    }    
                    """.trimIndent()

                sharedPrefs.edit().putString("visitedPlacesJson", visitedPlacesJSON).commit()
            }
        }

        val ivSavePlace = findViewById<ImageView>(R.id.ivHeart)
        ivSavePlace.setOnClickListener {
            if(ivSavePlace.tag == "Empty") {
                ivSavePlace.tag = "Filled"
                ivSavePlace.setImageResource(R.drawable.iconheartred)
                Toast.makeText(this@PlaceInformationActivity, "Added to saved places.", Toast.LENGTH_SHORT).show()
            } else if(ivSavePlace.tag == "Filled") {
                ivSavePlace.tag = "Empty"
                ivSavePlace.setImageResource(R.drawable.iconheart)
                Toast.makeText(this@PlaceInformationActivity, "Removed from saved places.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}