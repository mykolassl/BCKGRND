package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.MapView
import kotlin.math.roundToInt

class PlaceTouchListener(context: Context, mapView: MapView) : DefaultMapViewOnTouchListener(context, mapView) {
    private val m = mapView
    private val ctx = context

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val clickPoint = Point(e.x.roundToInt(), e.y.roundToInt())
        val tolerance = 10.0

        handleFeatureLayerClick(clickPoint, tolerance)
        handleCustomFeatureLayerClick(clickPoint, tolerance)

        return super.onSingleTapUp(e)
    }

    private fun handleFeatureLayerClick(p: Point, tol: Double) {
        val identifyLayerResultListenableFuture = m.identifyLayerAsync(
            m.map.operationalLayers[0],
            p,
            tol,
            false,
            1
        )
        identifyLayerResultListenableFuture.addDoneListener {
            try {
                val identifyLayerResult = identifyLayerResultListenableFuture.get()

                for (element in identifyLayerResult.elements) {
                    val feature = element as Feature
                    val attr = feature.attributes
                    val keys: Set<String> = attr.keys
                    val intent = Intent(ctx, PlaceInformationActivity::class.java)

                    for(key in keys) {
                        intent.putExtra(key, attr[key].toString())
                    }

                    ctx.startActivity(intent)

                    val envelope = feature.geometry.extent
                    mMapView.setViewpointGeometryAsync(envelope, 200.0)
                }
            } catch(_: Exception) {

            }
        }
    }

    private fun handleCustomFeatureLayerClick(p: Point, tol: Double) {
        val identifyLayerResultListenableFuture = m.identifyLayerAsync(
            m.map.operationalLayers[1],
            p,
            tol,
            false,
            1
        )
        identifyLayerResultListenableFuture.addDoneListener {
            try {
                val identifyLayerResult = identifyLayerResultListenableFuture.get()

                for (element in identifyLayerResult.elements) {
                    val feature = element as Feature
                    val attr = feature.attributes
                    val keys: Set<String> = attr.keys
                    val intent = Intent(ctx, PlaceInformationActivity::class.java)

                    for(key in keys) {
                        intent.putExtra(key, attr[key].toString())
                    }
                    intent.putExtra("xid", "-1")

                    ctx.startActivity(intent)

                    val envelope = feature.geometry.extent
                    mMapView.setViewpointGeometryAsync(envelope, 200.0)
                }
            } catch(_: Exception) {

            }
        }
    }
}