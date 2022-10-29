package com.example.bckgrnd

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MESSAGE", "Sign in created")

        val ivCamera = view.findViewById<ImageView>(R.id.ivCamera)
        ViewCompat.setTransitionName(ivCamera, "ivCamera")

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
//        TODO fix shared image animations
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)


    }
}