package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.beust.klaxon.Klaxon
import com.example.bckgrnd.Model.tblLocationResponse
import com.example.bckgrnd.Model.tblUser
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCurrentPass = view.findViewById<EditText>(R.id.etCurrentPass)
        val etNewPass = view.findViewById<EditText>(R.id.etNewPass)
        val etNewPassRepeat = view.findViewById<EditText>(R.id.etNewPassRepeat)
        val btnSubmit = view.findViewById<Button>(R.id.btnSave)

        btnSubmit.setOnClickListener {
            if (etNewPass.text.toString() != etNewPassRepeat.text.toString()) {
                Toast.makeText(activity, "Your new passwords don't match", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPreferences = requireActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                val userID = sharedPreferences.getInt("id", -1)
                val user = tblUser(
                    sharedPreferences.getString("userName", ""),
                    sharedPreferences.getString("userMail", ""),
                    etCurrentPass.text.toString(),
                    "",
                    userID
                )

                val iApi = RetroFitClient.getInstance().create(IApi::class.java)
                val compositeDisposable = CompositeDisposable()
                compositeDisposable.addAll(iApi.changePassword(userID, etNewPass.text.toString(), user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {s ->
                            activity?.finish()
                            Toast.makeText(activity, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        },
                        {t: Throwable ->
                            Toast.makeText(activity, "Wrong password", Toast.LENGTH_LONG).show()
                        })
                )
            }
        }

        // Transition between fragments
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }
}