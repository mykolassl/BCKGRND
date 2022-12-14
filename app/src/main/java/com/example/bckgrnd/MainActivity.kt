package com.example.bckgrnd

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.beust.klaxon.Klaxon
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.arcgisservices.LabelDefinition
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.labeling.ArcadeLabelExpression
import com.esri.arcgisruntime.mapping.view.*
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleRenderer
import com.esri.arcgisruntime.symbology.TextSymbol
import com.example.bckgrnd.AllPlaces.AllPlaces
import com.example.bckgrnd.databinding.ActivityMainBinding
import java.io.StringReader
import java.util.*


class MainActivity : AppCompatActivity() {
    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapView
    }

    private val locationDisplay: LocationDisplay by lazy {
        mapView.locationDisplay
    }

    private val envelope = Envelope(
        Point(25.107056, 54.577634, SpatialReferences.getWgs84()),
        Point(25.518219, 54.803851, SpatialReferences.getWgs84()))

    private fun setApiKeyForApp() {
        ArcGISRuntimeEnvironment.setApiKey(getString(R.string.arcgis_api_key))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupMap() {
        val featureLayer = FeatureLayer(ServiceFeatureTable("https://services8.arcgis.com/rP95XxeljMturhl8/arcgis/rest/services/response_1669309445062/FeatureServer/0"))
        val simpleSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 5f)

        featureLayer.renderer = SimpleRenderer(simpleSymbol)
        featureLayer.isLabelsEnabled = true
        featureLayer.labelDefinitions.add(makeLabelDefinition("name"))

        val map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC).apply {
            maxExtent = envelope
            minScale = 200000.0
            operationalLayers.add(featureLayer)
        }

        mapView.map = map

        locationDisplay.addDataSourceStatusChangedListener {
            // if LocationDisplay isn't started or has an error
            if (!it.isStarted && it.error != null) {
                // check permissions to see if failure may be due to lack of permissions
                requestPermissions(it)
            }
        }

        locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.RECENTER
        locationDisplay.startAsync()

        //mapView.setViewpoint(Viewpoint(envelope))

        val listener = PlaceTouchListener(this, mapView)
        mapView.onTouchListener = listener
    }

    private fun makeLabelDefinition(labelAttribute: String): LabelDefinition {
        val labelTextSymbol = TextSymbol().apply {
            color = Color.RED
            size = 8.0f
            haloColor = Color.WHITE
            haloWidth = 0.5f
            fontFamily = "Arial"
            fontStyle = TextSymbol.FontStyle.ITALIC
            fontWeight = TextSymbol.FontWeight.NORMAL
        }
        val labelExpression = ArcadeLabelExpression("\$feature.$labelAttribute")

        return LabelDefinition(labelExpression, labelTextSymbol)
    }

    private fun requestPermissions(dataSourceStatusChangedEvent: LocationDisplay.DataSourceStatusChangedEvent) {
        val requestCode = 2
        val reqPermissions = arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
        // fine location permission
        val permissionCheckFineLocation =
            ContextCompat.checkSelfPermission(this@MainActivity, reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED
        // coarse location permission
        val permissionCheckCoarseLocation =
            ContextCompat.checkSelfPermission(this@MainActivity, reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED
        if (!(permissionCheckFineLocation && permissionCheckCoarseLocation)) { // if permissions are not already granted, request permission from the user
            ActivityCompat.requestPermissions(this@MainActivity, reqPermissions, requestCode)
        } else {
            val message = String.format(
                "Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                    .source.locationDataSource.error.message
            )
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // if request is cancelled, the results array is empty
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationDisplay.startAsync()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Couldn't identify your location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun EditText.onSubmit(func: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            Log.i("MESSAGE", actionId.toString())
            if (actionId == 6) func()
            true
        }
    }

    private fun searchPlaces(text: String) {
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
        featuresArray?.forEach { e ->
            if (e.name.contains(text)) {
                placesXIDs += e.xid
                placesNames += e.name
                Log.i("MESSAGE", e.name)
            }
        }
//        val intent = Intent(this@MainActivity, SearchActivity::class.java)
//        intent.putExtra("placeNames", Klaxon().toJsonString(placesNames))
//        intent.putExtra("placeXIDs", Klaxon().toJsonString(placesXIDs))
//        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setApiKeyForApp()
        setupMap()

        setContentView(activityMainBinding.root)

        // Search text field logic
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.onSubmit {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            intent.putExtra("searchQuery", etSearch.text.toString())
            startActivity(intent)
//            searchPlaces(etSearch.text.toString())
        }

        // Menu button click logic
        val btnBurgerMenu = findViewById<ImageView>(R.id.ivBurger)
        btnBurgerMenu.setOnClickListener {
            val menu = findViewById<ConstraintLayout>(R.id.viewBurger)
            menu.visibility = View.VISIBLE
        }

        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        btnSignOut.setOnClickListener {
            val preferences = this.getSharedPreferences("isLogged", MODE_PRIVATE)
            with(preferences.edit()) {
                putBoolean("isLogged", false)
                commit()
            }
            this.finish()
            startActivity(Intent(this, SignOutActivity::class.java))
        }

        val btnProfile = findViewById<Button>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        val btnUpload = findViewById<Button>(R.id.btnUpload)
        btnUpload.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

        val btnCloseMenu = findViewById<ImageView>(R.id.ivCloseMenu)
        btnCloseMenu.setOnClickListener {
            val menu = findViewById<ConstraintLayout>(R.id.viewBurger)
            menu.visibility = View.GONE
        }

        // Navigation button click logic
//        val btnPlacesToVisit = findViewById<Button>(R.id.btnPlaces)
//        btnPlacesToVisit.setOnClickListener {
//            startActivity(Intent(this@MainActivity, PlacesActivity::class.java))
//        }

        val btnVisitedPlaces = findViewById<Button>(R.id.btnVisited)
        btnVisitedPlaces.setOnClickListener {
            startActivity(Intent(this@MainActivity, VisitedPlacesActivity::class.java))
        }

        val btnAttractions = findViewById<Button>(R.id.btnAttractions)
        btnAttractions.setOnClickListener {
            startActivity(Intent(this@MainActivity, AttractionsActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.dispose()
    }

}