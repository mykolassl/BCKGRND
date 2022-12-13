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
import com.example.bckgrnd.Model.tblUser
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    lateinit var iApi: IApi
    var compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MESSAGE", "Sign up created")

        val btnSignUp = view.findViewById<Button>(R.id.btnSignUp)
        val etUserFirstName = view.findViewById<EditText>(R.id.etFirstName)
        val etUserLastName = view.findViewById<EditText>(R.id.etLastName)
        val etUserEmail = view.findViewById<EditText>(R.id.etEmail)
        val etUserPassword = view.findViewById<EditText>(R.id.etPassword)

        iApi = RetroFitClient.getInstance().create(IApi::class.java)

        btnSignUp?.setOnClickListener {
            val userName = "${etUserFirstName.text} ${etUserLastName.text}"
            val userMail = etUserEmail.text.toString()
            val userPass = etUserPassword.text.toString()
            val user = tblUser(UserName= userName, UserMail = userMail, UserPass = userPass)
            compositeDisposable.addAll(iApi.registerUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({s ->
                    if(s.contains("successfully")) {
                        activity?.finish()
                    }

                    val sharedPreferences = requireActivity().applicationContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("userName", userName)
                        putString("userMail", userMail)
                        commit()
                    }

                    Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(context, MainActivity::class.java))

                },
                {t: Throwable? ->
                    Toast.makeText(activity, t!!.message, Toast.LENGTH_SHORT).show()
                }))
        }

        // Transition between fragments
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }
}