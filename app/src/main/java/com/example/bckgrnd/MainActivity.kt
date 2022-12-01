package com.example.bckgrnd

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Point
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.arcgisservices.LabelDefinition
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.labeling.ArcadeLabelExpression
import com.esri.arcgisruntime.mapping.view.*
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleRenderer
import com.esri.arcgisruntime.symbology.TextSymbol
import com.example.bckgrnd.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.roundToInt


//class MyTouchListener(context: Context, mapView: MapView) : DefaultMapViewOnTouchListener(context, mapView) {
//    private val m = mapView
//    private val ctx = context
//    private val mCallout = m.callout
//
//    override fun onSingleTapUp(e: MotionEvent): Boolean {
//        val p = Point(e.x.roundToInt(), e.y.roundToInt())
//        val tolerance = 10.0
//
//        val identifyLayerResultListenableFuture = m.identifyLayerAsync(m.map.operationalLayers[0], p, tolerance, false, 1)
//        identifyLayerResultListenableFuture.addDoneListener {
//            try {
//                val identifyLayerResult = identifyLayerResultListenableFuture.get()
//
//                for (element in identifyLayerResult.elements) {
//                    val feature = element as Feature
//                    val attr = feature.attributes
//                    val keys: Set<String> = attr.keys
//                    val intent = Intent(ctx, PlaceInformationActivity::class.java)
//
//                    for(key in keys) {
//                        intent.putExtra(key, attr[key].toString())
//                    }
//
//                    ctx.startActivity(intent)
//
//                    val envelope = feature.geometry.extent
//                    mMapView.setViewpointGeometryAsync(envelope, 200.0)
//                }
//            } catch(_: Exception) {
//
//            }
//        }
//
//        return super.onSingleTapUp(e)
//    }
//}

class MainActivity : AppCompatActivity() {
    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapView
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
        val simpleSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10f)

        featureLayer.renderer = SimpleRenderer(simpleSymbol)
        featureLayer.isLabelsEnabled = true
        featureLayer.labelDefinitions.add(makeLabelDefinition("name"))

        val map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC).apply {
            maxExtent = envelope
            minScale = 200000.0
            operationalLayers.add(featureLayer)
        }

        mapView.map = map
        mapView.setViewpoint(Viewpoint(envelope))

        val listener = PlaceTouchListener(this, mapView)
        mapView.onTouchListener = listener
    }

    private fun makeLabelDefinition(labelAttribute: String): LabelDefinition {
        val labelTextSymbol = TextSymbol().apply {
            color = Color.RED
            size = 11.0f
            haloColor = Color.WHITE
            haloWidth = 0.5f
            fontFamily = "Arial"
            fontStyle = TextSymbol.FontStyle.ITALIC
            fontWeight = TextSymbol.FontWeight.NORMAL
        }
        val labelExpression = ArcadeLabelExpression("\$feature.$labelAttribute")

        return LabelDefinition(labelExpression, labelTextSymbol)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setApiKeyForApp()
        setupMap()

        setContentView(activityMainBinding.root)

        // Menu button click logic
        val btnBurgerMenu = findViewById<ImageView>(R.id.ivBurger)
        btnBurgerMenu.setOnClickListener {
            val menu = findViewById<ConstraintLayout>(R.id.viewBurger)
            menu.visibility = View.VISIBLE
        }

        val btnCloseMenu = findViewById<ImageView>(R.id.ivCloseMenu)
        btnCloseMenu.setOnClickListener {
            val menu = findViewById<ConstraintLayout>(R.id.viewBurger)
            menu.visibility = View.INVISIBLE
        }

        // Navigation button click logic
        val btnPlacesToVisit = findViewById<Button>(R.id.btnPlaces)
        btnPlacesToVisit.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlacesActivity::class.java))
        }

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