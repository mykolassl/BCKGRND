package com.example.bckgrnd

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MESSAGE", "Welcome created")

        val btnContinueLogin = view.findViewById<Button>(R.id.btnContinueLogin)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.slide_left)

        val ivCamera = view.findViewById<ImageView>(R.id.ivCamera)
        ViewCompat.setTransitionName(ivCamera, "ivCamera")

        btnContinueLogin.setOnClickListener {
            Log.i("MESSAGE", "Btn clicked")

            if(savedInstanceState == null) {
                parentFragmentManager.commit {
                    replace<SignInFragment>(R.id.fragment_sign_in_view)
                    setReorderingAllowed(true)
                    addToBackStack("com.example.bckgrnd.WelcomeActivity")

                    val fragmentWelcome = WelcomeFragment()
                    addSharedElement(ivCamera, "ivCamera")

                    val fragment = parentFragmentManager.findFragmentById(R.id.fragment_welcome_view) as WelcomeFragment
                    detach(fragment)
                }
            }
        }

    }
}