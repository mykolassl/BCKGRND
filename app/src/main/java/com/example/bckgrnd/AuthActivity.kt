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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getPreferences(Context.MODE_PRIVATE) ?: return
        if(sharedPrefs.getBoolean("isLogged", false)) {
            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
        }

        if(savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<WelcomeFragment>(R.id.fragment_welcome_view)
            }
        }
    }
}