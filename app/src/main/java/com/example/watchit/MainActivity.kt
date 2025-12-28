package com.example.watchit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.watchit.data.di.AppModule
import com.example.watchit.data.local.AppDatabase
import com.example.watchit.data.local.LibraryEntity
import com.example.watchit.data.local.MovieEntity
import com.example.watchit.databinding.ActivityMainBinding
import com.example.watchit.util.extractUrl
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var root: ConstraintLayout
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        root = findViewById(R.id.homeRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }


        handleShareIntent(intent)

    }

    private fun handleShareIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain"){
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
            if (sharedText.isNotEmpty()){
                val url = extractUrl(sharedText)
                if (url != null){
                    navigateToAddMovieWithSharedUrl(url)

                }else{
                    Toast.makeText(this, "No URL fond in shared text", Toast.LENGTH_SHORT).show()
                }
                intent.action = null

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    private fun navigateToAddMovieWithSharedUrl(sharedText: String) {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController


            val bundle = bundleOf(
                "libraryId" to -1L,
                "sharedUrl" to sharedText
            )
        try {
            navController.navigate(R.id.addMovieFragment, bundle)
        } catch (e: Exception) {
            Log.e("NAV_ERROR", "Failed to navigate to addMovieFragment", e)
        }
           // navController.navigate(R.id.addMovieFragment, bundle)




    }

}