package com.example.bckgrnd

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MESSAGE", "Sign in created")

        // Create a link to create account fragment
        val text = SpannableString("New member? Create an account")
        val link: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                parentFragmentManager.commit {
                    replace<SignUpFragment>(R.id.fragment_sign_up_view)
                    setReorderingAllowed(true)
                    addToBackStack("com.example.bckgrnd.SignUpFragment")

                    val fragment = parentFragmentManager.findFragmentById(R.id.fragment_sign_in_view) as SignInFragment
                    detach(fragment)
                }
            }
        }
        text.setSpan(link, 12, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tvCreateAcc = view.findViewById<TextView>(R.id.tvCreateAcc)
        tvCreateAcc.text = text
        tvCreateAcc.movementMethod = LinkMovementMethod.getInstance()

        // Transition between fragments
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }
}