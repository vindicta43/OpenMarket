package com.alperen.openmarket.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alperen.openmarket.R
import com.alperen.openmarket.databinding.ActivityMainBinding
import com.alperen.openmarket.utils.Constants
import com.alperen.openmarket.utils.FirebaseInstance

class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerMain) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPrefForget = getSharedPreferences(Constants.FORGET_ME, Context.MODE_PRIVATE)
        val isForget = sharedPrefForget.getBoolean(Constants.FORGET_ME, false)
        if (isForget) {
            FirebaseInstance.logout()
        }

        val sharedPref = getSharedPreferences(Constants.APP_INIT, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(Constants.APP_INIT, false).apply()
    }
}