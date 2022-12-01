package com.example.bckgrnd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VisitedPlaceAdapter(
    var visitedPlaces: List<VisitedPlace>
): RecyclerView.Adapter<VisitedPlaceAdapter.PlaceViewHolder>() {
    inner class PlaceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.visited_place_row_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.itemView.apply {
            val tvPlaceName = findViewById<TextView>(R.id.btnVisitedPlace)
            tvPlaceName.text = visitedPlaces[position].title
        }
    }

    override fun getItemCount(): Int {
        return visitedPlaces.size
    }


}