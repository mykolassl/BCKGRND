package com.example.bckgrnd

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.labeling.LabelExpression
import com.esri.arcgisruntime.mapping.labeling.SimpleLabelExpression
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.TextSymbol
import com.example.bckgrnd.databinding.ActivityMainBinding


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

    private fun setupMap() {
        val map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC).apply {
            maxExtent = envelope
            minScale = 200000.0
        }

        mapView.map = map
        mapView.setViewpoint(Viewpoint(envelope))
    }

    private fun addGraphics() {
        val graphicsOverlay = GraphicsOverlay()
        mapView.graphicsOverlays.add(graphicsOverlay)

        val point = Point(25.241123, 54.660329, SpatialReferences.getWgs84())
        val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)
        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, -0xff9c01, 2f)
        simpleMarkerSymbol.outline = blueOutlineSymbol

        val pointGraphic = Graphic(point, simpleMarkerSymbol)
        graphicsOverlay.graphics.add(pointGraphic)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setApiKeyForApp()
        setupMap()
        addGraphics()

        setContentView(activityMainBinding.root)
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