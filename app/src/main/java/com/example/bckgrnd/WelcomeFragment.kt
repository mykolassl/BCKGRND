package com.example.bckgrnd

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MESSAGE", "Welcome created")

        val btnContinueLogin = view.findViewById<Button>(R.id.btnContinueLogin)
        val btnGetStarted = view.findViewById<Button>(R.id.btnGetStarted)

        btnContinueLogin.setOnClickListener {
            if(savedInstanceState == null) {
                parentFragmentManager.commit {
                    //val ivCamera = view.findViewById<ImageView>(R.id.ivCamera)
                    //addSharedElement(ivCamera, "ivCamera_start")

                    replace<SignInFragment>(R.id.fragment_sign_in_view)
                    setReorderingAllowed(true)
                    addToBackStack("com.example.bckgrnd.WelcomeFragment")

                    val fragment = parentFragmentManager.findFragmentById(R.id.fragment_welcome_view) as WelcomeFragment
                    detach(fragment)
                }
            }
        }

        btnGetStarted.setOnClickListener {
            if(savedInstanceState == null) {
                parentFragmentManager.commit {
                    replace<SignUpFragment>(R.id.fragment_sign_up_view)
                    setReorderingAllowed(true)
                    addToBackStack("com.example.bckgrnd.WelcomeFragment")

                    val fragment = parentFragmentManager.findFragmentById(R.id.fragment_welcome_view) as WelcomeFragment
                    detach(fragment)
                }
            }
        }

        // Transition between fragments
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }
}