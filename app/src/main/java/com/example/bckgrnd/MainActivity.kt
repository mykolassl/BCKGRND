package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView

import com.example.bckgrnd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapView
    }

    private fun setApiKeyForApp() {
        ArcGISRuntimeEnvironment.setApiKey(getString(R.string.arcgis_api_key))
    }

    private fun setupMap() {
        val map = ArcGISMap(BasemapStyle.ARCGIS_STREETS_NIGHT)
        mapView.map = map
        mapView.setViewpoint(Viewpoint(54.687157, 25.279652, 72000.0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setApiKeyForApp()
        //setupMap()

        setContentView(activityMainBinding.root)
        //setContentView(R.layout.activity_main)

        activityMainBinding.btnPlaces.setOnClickListener {
            Log.i("MESSAGE", "Places button clicked")
        }
    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }

}