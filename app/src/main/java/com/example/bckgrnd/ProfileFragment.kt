package com.example.bckgrnd

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnUpload = view.findViewById<Button>(R.id.button8)
        btnUpload.setOnClickListener {
            parentFragmentManager.commit {
                replace<UploadFragment>(R.id.fragment_upload)
                setReorderingAllowed(true)
                addToBackStack("com.example.bckgrnd.ProfileFragment")

                val fragment = parentFragmentManager.findFragmentById(R.id.fragment_user_profile) as ProfileFragment
                detach(fragment)
            }
        }

        val btnChange = view.findViewById<Button>(R.id.button6)
        btnChange.setOnClickListener {
            parentFragmentManager.commit {
                replace<ChangePasswordFragment>(R.id.fragment_change_password)
                setReorderingAllowed(true)
                addToBackStack("com.example.bckgrnd.ProfileFragment")

                val fragment = parentFragmentManager.findFragmentById(R.id.fragment_user_profile) as ProfileFragment
                detach(fragment)
            }
        }

        val btnSaved = view.findViewById<Button>(R.id.button7)
        btnSaved.setOnClickListener {
            parentFragmentManager.commit {
                replace<SavedPlacesFragment>(R.id.fragment_saved_places)
                setReorderingAllowed(true)
                addToBackStack("com.example.bckgrnd.ProfileFragment")

                val fragment = parentFragmentManager.findFragmentById(R.id.fragment_user_profile) as ProfileFragment
                detach(fragment)
            }
        }

        // Transition between fragments
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }
}