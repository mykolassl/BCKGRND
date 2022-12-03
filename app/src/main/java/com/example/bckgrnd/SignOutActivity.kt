package com.example.bckgrnd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignOutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_out)

        val btnReturn = findViewById<Button>(R.id.btnReturn)
        btnReturn.setOnClickListener {
            this.finish()
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }
}