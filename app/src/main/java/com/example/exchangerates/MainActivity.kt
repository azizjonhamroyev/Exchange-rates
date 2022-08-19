package com.example.exchangerates

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.exchangerates.databinding.ActivityMainBinding
import com.example.exchangerates.service.UploadWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        val uploadCurrency =
            PeriodicWorkRequest.Builder(UploadWorker::class.java, 5, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(uploadCurrency)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.appBarMain.contentMain.bottomBar.setupWithNavController(navController)
        binding.appBarMain.contentMain.bottomBar.itemIconTintList = null
    }


    override fun onResume() {
        binding.appBarMain.toolbar.setNavigationIcon(R.drawable.ic_menu)
        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.appBarMain.toolbar.setNavigationIcon(
                R.drawable.ic_menu
            )
        }
        super.onResume()
    }

    override fun onBackPressed() {
        onStop()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}