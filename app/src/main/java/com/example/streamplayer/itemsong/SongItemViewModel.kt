package com.example.streamplayer.itemsong

import android.app.Application
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import com.bumptech.glide.Glide
import com.example.streamplayer.R
import com.example.streamplayer.listsongs.SongViewModel
import com.example.streamplayer.model.Tracks
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

class SongItemViewModel(application: Application): AndroidViewModel(application) {
    lateinit var player: ExoPlayer
//    private val listSongViewModel: SongViewModel by activityViewModels()
  //  var trackList = emptyList<Tracks>()
 //   val trackList = listSongViewModel.trackListLiveData.value!!

    fun playingPlayer(positionFromList: Int, trackList: List<Tracks>){
        val uri = trackList?.get(positionFromList)?.previewURL.toString()
        player = SimpleExoPlayer.Builder(getApplication()).build()
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        //     player.play()

    }




}