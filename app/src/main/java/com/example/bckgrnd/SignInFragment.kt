package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.bckgrnd.Model.tblUser
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.reflect.typeOf

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    lateinit var iApi: IApi
    var compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSignIn = view.findViewById<Button>(R.id.btnSignIn)
        val etUserEmail = view.findViewById<EditText>(R.id.etEmail)
        val etUserPassword = view.findViewById<EditText>(R.id.etPassword)

        iApi = RetroFitClient.getInstance().create(IApi::class.java)

        btnSignIn?.setOnClickListener {
            val userReq = tblUser(UserMail = etUserEmail.text.toString(), UserPass = etUserPassword.text.toString())

            compositeDisposable.addAll(iApi.loginUser(userReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {s ->
                        val sharedPrefs = activity?.getSharedPreferences("isLogged", Context.MODE_PRIVATE)
                        with(sharedPrefs!!.edit()) {
                            putBoolean("isLogged", true)
                            commit()
                        }

                        val sharedPreferences = requireActivity().applicationContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("userName", s.UserName)
                            putString("userMail", s.UserMail)
                            putInt("id", s.ID!!)
                            Log.i("MESSAGE", s.ID.toString())
                            commit()
                        }

                        Log.i("MESSAGE", sharedPreferences.getInt("id", -1).toString())

                        activity?.finish()
                        Toast.makeText(activity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(activity, MainActivity::class.java))
                    },
                    {t: Throwable ->
                        Toast.makeText(activity, "Wrong email or password", Toast.LENGTH_LONG).show()
                    })
            )
        }

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