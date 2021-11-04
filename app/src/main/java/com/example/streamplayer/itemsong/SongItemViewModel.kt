package com.example.streamplayer.itemsong

import android.app.Application
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.streamplayer.R
import com.example.streamplayer.audioservice.MusicRepository
import com.example.streamplayer.listsongs.SongViewModel
import com.example.streamplayer.model.Tracks
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongItemViewModel(application: Application): AndroidViewModel(application) {

    var trackItemLiveData: MutableLiveData<List<Tracks>> = MutableLiveData()
    var trackLiveData: MutableLiveData<Tracks> = MutableLiveData()

        init {
            CoroutineScope(Dispatchers.IO).launch {
                val repository = MusicRepository(application)
                val track = repository.getCurrent()
                trackLiveData.postValue(track)

                val list = repository.getTrackList()
                    trackItemLiveData.postValue(list)
                Log.d("MyLog", "trackItemList: ${trackItemLiveData.value}")
            }
        }

        fun getTrackList () = trackItemLiveData


//    private val listSongViewModel: SongViewModel by activityViewModels()
  //  var trackList = emptyList<Tracks>()
 //   val trackList = listSongViewModel.trackListLiveData.value!!

    lateinit var player: ExoPlayer
    fun playingPlayer(positionFromList: Int, trackList: List<Tracks>){
        val uri = trackList?.get(positionFromList)?.previewURL.toString()
        player = SimpleExoPlayer.Builder(getApplication()).build()
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        //     player.play()

    }




}