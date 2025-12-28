package com.example.watchit.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.watchit.NavGraphDirections
import com.example.watchit.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this, "Hey I'm In ShareActivity", Toast.LENGTH_SHORT).show()
        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        navHost.view?.post {
            val action = NavGraphDirections.actionGlobalAddMovieFragment(libraryId = -1L,sharedText)
            navController.navigate(action)
        }


    }
}