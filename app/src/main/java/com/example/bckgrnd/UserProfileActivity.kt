package com.example.bckgrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.add
import androidx.fragment.app.commit

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if(savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ProfileFragment>(R.id.fragment_user_profile)
            }
        }
    }
}