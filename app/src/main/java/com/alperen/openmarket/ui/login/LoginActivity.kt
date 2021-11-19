package com.alperen.openmarket.ui.login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPref = getSharedPreferences(Constants.APP_INIT, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(Constants.APP_INIT, false).apply()
    }
}