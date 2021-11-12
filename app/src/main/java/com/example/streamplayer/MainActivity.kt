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
  //  setupActionBarWithNavController(findNavController(R.id.songList))

     //   OpenSongItemFragment()
     //   OpenSongListFragment()





    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.songList)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
/*
    fun OpenSongItemFragment(){
        val songItemFragment = SongItem()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, songItemFragment)
            .commit()
    }
    fun OpenSongListFragment(){
        val songListFragment = SongList()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, songListFragment)
            .commit()
    }*/


}