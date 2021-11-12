package com.example.streamplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.findNavController
import com.example.streamplayer.audioservice.MusicRepository

class MainActivity : AppCompatActivity(), ViewModelStoreOwner {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.songList)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}