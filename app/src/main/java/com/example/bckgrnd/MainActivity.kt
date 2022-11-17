package com.example.bckgrnd

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.TextSymbol
import com.example.bckgrnd.databinding.ActivityMainBinding

class MyTouchListener(context: Context, mapView: MapView) : DefaultMapViewOnTouchListener(context, mapView) {
    private val m = mapView
    private val ctx = context

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Toast.makeText(ctx, "${e.x} ${e.y}", Toast.LENGTH_SHORT).show()

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
        val map = ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC).apply {
            maxExtent = envelope
            minScale = 200000.0
        }

        mapView.map = map
        mapView.setViewpoint(Viewpoint(envelope))

        val ls = MyTouchListener(this, mapView)
        mapView.onTouchListener = ls
    }

    private fun createPoint(xCoord: Double, yCoord: Double): Graphic {
        val point = Point(xCoord, yCoord, SpatialReferences.getWgs84())
        val symbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, -0xff9c01, 10f)
        return Graphic(point, symbol)
    }

    private fun createPointText(xCoord: Double, yCoord: Double, text: String): Graphic {
        val point = Point(xCoord, yCoord, SpatialReferences.getWgs84())
        val symbol = TextSymbol(10f,
            text,
            -0xa8cd,
            TextSymbol.HorizontalAlignment.CENTER,
            TextSymbol.VerticalAlignment.BOTTOM)
        return Graphic(point, symbol)
    }

    private fun addGraphics() {
        val graphicsOverlay = GraphicsOverlay()
        mapView.graphicsOverlays.add(graphicsOverlay)

        //val point = Point(25.241123, 54.660329, SpatialReferences.getWgs84())
        //val marker = TextSymbol(10f, "Laba diena", -0xa8cd, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.BOTTOM)
        //val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xff9c01, 10f)

        //var pointGraphic = Graphic(point, simpleMarkerSymbol)
        graphicsOverlay.graphics.add(createPoint(25.2884191, 54.6866518))
        //pointGraphic = Graphic(point, marker)
        graphicsOverlay.graphics.add(createPointText(25.2884191, 54.6866518, "Gedimino pokstas"))
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