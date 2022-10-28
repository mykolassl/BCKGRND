package com.example.bckgrnd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.app.Activity
import android.content.Context
import android.util.Log

class WelcomeActivity : AppCompatActivity() {
    lateinit var buttonLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        buttonLogin = findViewById(R.id.button)
        buttonLogin.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, SignInActivity::class.java))
        }
    }
}