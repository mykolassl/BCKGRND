package com.example.bckgrnd

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.*
import com.example.bckgrnd.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.roundToInt


class MyTouchListener(context: Context, mapView: MapView) : DefaultMapViewOnTouchListener(context, mapView) {
    private val m = mapView
    private val ctx = context
    private val mCallout = m.callout

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        if(mCallout.isShowing) mCallout.dismiss()

        val p = Point(e.x.roundToInt(), e.y.roundToInt())
        val tolerance = 10.0

        val identifyLayerResultListenableFuture = m.identifyLayerAsync(m.map.operationalLayers[0], p, tolerance, false, 1)
        identifyLayerResultListenableFuture.addDoneListener {
            try {
                val identifyLayerResult = identifyLayerResultListenableFuture.get()

                val calloutContent = TextView(ctx)
                calloutContent.setTextColor(Color.BLACK)
                calloutContent.isSingleLine = false
                calloutContent.isVerticalScrollBarEnabled = true
                calloutContent.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
                calloutContent.movementMethod = ScrollingMovementMethod()
                calloutContent.setLines(2)

                for (element in identifyLayerResult.elements) {
                    val feature = element as Feature
                    val attr = feature.attributes
                    val keys: Set<String> = attr.keys

                    for (key in keys) {
                        if(key != "name") continue

                        val value = attr[key]
                        calloutContent.append("$value")
                    }

                    val envelope = feature.geometry.extent
                    mMapView.setViewpointGeometryAsync(envelope, 200.0)

                    mCallout.location = envelope.center
                    mCallout.content = calloutContent
                    mCallout.show()
                }
            } catch(_: Exception) {

            }
        }

        return super.onSingleTapUp(e)
    }
}

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
        val map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC).apply {
            maxExtent = envelope
            minScale = 200000.0
            operationalLayers.add(featureLayer)
        }

        mapView.map = map
        mapView.setViewpoint(Viewpoint(envelope))

        val ts = MyTouchListener(this, mapView)
        mapView.onTouchListener = ts
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setApiKeyForApp()
        setupMap()

        setContentView(activityMainBinding.root)
        // Button click logic

        val btnBurgerMenu = findViewById<ImageView>(R.id.ivBurger)
        btnBurgerMenu.setOnClickListener {
            Log.i("MESSAGE", "LOL")
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