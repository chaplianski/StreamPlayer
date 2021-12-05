package com.example.streamplayer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.findNavController
import com.example.streamplayer.audioservice.MusicRepository

class MainActivity : AppCompatActivity(), ViewModelStoreOwner {

    lateinit var themeState: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        themeState = getSharedPreferences("theme", Context.MODE_PRIVATE)
        val themeMode = themeState?.getString("theme", "day").toString()
        if (themeMode == "night") {
            this.window?.statusBarColor = ContextCompat.getColor(this,
                android.R.color.background_dark
            )
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }


    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.songList)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toobar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var theme = ""
        when(item.itemId){
            R.id.bt_day_theme -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                theme = "day"
            }
            R.id.bt_night_theme -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                theme = "night"
            }
        }
        val editor: SharedPreferences.Editor = themeState.edit()
        editor.putString("theme", theme)
        editor.apply()
        return super.onOptionsItemSelected(item)
    }

}