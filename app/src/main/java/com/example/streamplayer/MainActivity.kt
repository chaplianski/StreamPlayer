package com.example.streamplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
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
    companion object{
    fun getContext(activity: MainActivity){
        return getContext(activity)
    }
    }

}