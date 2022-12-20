package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.*
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().applicationContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE)

        val tvUserFirstName = view.findViewById<TextView>(R.id.tvUserFirstName)
        val tvUserLastName = view.findViewById<TextView>(R.id.tvUserLastName)
        val tvUserMail = view.findViewById<TextView>(R.id.tvUserMail)

        try {
            tvUserFirstName.text = sharedPreferences.getString("userName", "")?.split(" ")?.get(0) ?: ""
            tvUserMail.text = sharedPreferences.getString("userMail", "")
            tvUserLastName.text = sharedPreferences.getString("userName", "")?.split(" ")?.get(1) ?: ""
        } catch (_: Exception) {

        }

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

        val btnDelete = view.findViewById<Button>(R.id.button5)
        btnDelete.setOnClickListener {
            val popUp = view.findViewById<ConstraintLayout>(R.id.popUpDelete)
            popUp.visibility = VISIBLE
        }

        val btnConfirmDelete = view.findViewById<Button>(R.id.btnConfirmDelete)
        btnConfirmDelete.setOnClickListener {
            val iApi = RetroFitClient.getInstance().create(IApi::class.java)
            val userID = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE)!!.getInt("id", -1)

            if (userID == -1) return@setOnClickListener

            CompositeDisposable().addAll(iApi.deleteUser(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {s ->
                        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
                        val sharedPrefs = requireActivity().getSharedPreferences("isLogged", Context.MODE_PRIVATE)
                        with(sharedPrefs.edit()) {
                            putBoolean("isLogged", false)
                            commit()
                        }
                        activity?.finishAffinity()
                        activity?.startActivity(Intent(activity, AuthActivity::class.java))
                    },
                    {t: Throwable ->
                        Toast.makeText(activity, "Something went wrong while deleting your account, please try again.", Toast.LENGTH_SHORT).show()
                    })
            )
        }

        val btnCloseMenu = view.findViewById<ImageView>(R.id.ivCloseMenu)
        btnCloseMenu.setOnClickListener {
            val popUp = view.findViewById<ConstraintLayout>(R.id.popUpDelete)
            popUp.visibility = GONE
        }

        // Transition between fragments
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }
}