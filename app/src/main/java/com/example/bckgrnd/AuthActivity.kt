package com.example.bckgrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.add
import androidx.fragment.app.commit

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MESSAGE", "Auth created")

        if(savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<WelcomeFragment>(R.id.fragment_welcome_view)
            }
        }
    }
}