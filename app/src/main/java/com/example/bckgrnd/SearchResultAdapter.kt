package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchResultAdapter(
    var resultXIDs: List<String?>,
    var resultNames: List<String>,
    var ctx: Context
): RecyclerView.Adapter<SearchResultAdapter.PlaceViewHolder>() {
    inner class PlaceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.visited_place_row_item, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.itemView.apply {
            val btnPlaceName = findViewById<Button>(R.id.btnVisitedPlace)
            btnPlaceName.text = resultNames[position]
            btnPlaceName.setOnClickListener {
                if (position >= resultXIDs.size) {
                    val intent = Intent(ctx, PlaceInformationActivity::class.java)
                    intent.putExtra("xid", "-1")
                    intent.putExtra("name", resultNames[position])
                    Log.i("MESSAGE", "Daugiau")
                    ctx.startActivity(intent)
                } else {
                    val intent = Intent(ctx, PlaceInformationActivity::class.java)
                    intent.putExtra("xid", resultXIDs[position])
                    Log.i("MESSAGE", "Maziau")
                    ctx.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return resultNames.size
    }
}