package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VisitedPlaceAdapter(
    var visitedPlaces: List<Place>,
    var ctx: Context
): RecyclerView.Adapter<VisitedPlaceAdapter.PlaceViewHolder>() {
    inner class PlaceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.visited_place_row_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.itemView.apply {
            val btnPlaceName = findViewById<Button>(R.id.btnVisitedPlace)
            btnPlaceName.text = visitedPlaces[position].placeName
            btnPlaceName.setOnClickListener {
                val intent = Intent(ctx, PlaceInformationActivity::class.java)
                intent.putExtra("xid", visitedPlaces[position].xid)
                intent.putExtra("dbID", visitedPlaces[position].dbID)
                ctx.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return visitedPlaces.size
    }
}