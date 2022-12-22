package com.example.bckgrnd

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import java.io.StringReader

class SavedPlacesFragment : Fragment(R.layout.fragment_saved_places) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvPlaces = view.findViewById<RecyclerView>(R.id.rvPlaces)

        val sharedPrefs = requireActivity().applicationContext.getSharedPreferences("savedPlacesJson", Context.MODE_PRIVATE)
        val savedPlacesJSON = sharedPrefs.getString("savedPlacesJson", "")!!.trimIndent()

        if (savedPlacesJSON.isNotEmpty()) {
            val parsedPlaces = Klaxon().parseJsonObject(StringReader(savedPlacesJSON))
            val placesArray = parsedPlaces.array<Place>("data")!!.let { Klaxon().parseFromJsonArray<Place>(it) }

            val adapter = placesArray?.let { VisitedPlaceAdapter(it, requireContext()) }
            rvPlaces.adapter = adapter
            rvPlaces.layoutManager = LinearLayoutManager(requireContext())
        }

        // Transition between fragments
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }
}