package com.example.bckgrnd

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.bckgrnd.Model.tblUser
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {
    lateinit var iApi: IApi
    var compositeDisposable = CompositeDisposable()

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        //val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        val sharedPrefs = getPreferences(Context.MODE_PRIVATE) ?: return
        if(sharedPrefs.getBoolean("isLogged", false)) {
            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
        }

        val places = sharedPrefs.getStringSet("places", setOf<String?>(""))
        Log.i("MESSAGE", "${places}")
        with(sharedPrefs.edit()) {
            putStringSet("places", setOf<String?>("Katedra", "Gedimino bokstas"))
            apply()
        }
        Log.i("MESSAGE", "${sharedPrefs.getStringSet("places", setOf<String?>(""))}")

        if(savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<WelcomeFragment>(R.id.fragment_welcome_view)
            }
        }


//        iApi = RetroFitClient.getInstance().create(IApi::class.java)
//
//        btnSignIn.setOnClickListener {
//            val user = tblUser(UserMail = R.id.etEmail.toString(), UserPass = R.id.etPassword.toString())
//            compositeDisposable.addAll(iApi.loginUser(user)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {s ->
//                        Toast.makeText(this@AuthActivity, s, Toast.LENGTH_SHORT).show()
//                    },
//                    {t: Throwable ->
//                        Toast.makeText(this@AuthActivity, t!!.message, Toast.LENGTH_SHORT).show()
//                    })
//            )
//        }

//        btnSignUp.setOnClickListener {
//            val user = tblUser(UserName= "${R.id.etFirstName} ${R.id.etLastName}", UserMail = R.id.etEmail.toString(), UserPass = R.id.etPassword.toString())
//            compositeDisposable.addAll(iApi.registerUser(user)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({s ->
//                    if(s.contains("successfully")) {
//                        finish()
//                    }
//
//                    Toast.makeText(this@AuthActivity, s, Toast.LENGTH_SHORT).show()
//                },
//                {t: Throwable? ->
//                    Toast.makeText(this@AuthActivity, t!!.message, Toast.LENGTH_SHORT).show()
//                }))
//        }
    }
}