package com.example.streamplayer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.streamplayer.MainActivity
import com.example.streamplayer.RepositoryInstance
import com.example.streamplayer.audioservice.MusicRepository
import com.example.streamplayer.model.Tracks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

 class SongItemViewModel(application: Application): AndroidViewModel(application) {

 //   var trackItemLiveData: MutableLiveData<List<Tracks>> = MutableLiveData()
    var trackLiveData: MutableLiveData<Tracks> = MutableLiveData()
 //   val repository = RepositoryInstance.getMusicRepository()
    val repository = MusicRepository(application)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val track = repository?.getCurrent()
            trackLiveData.postValue(track)

           Log.d("MyLog", "sViewModel track: $track")
           Log.d("MyLog", "sViewModel trackLiveData.value: ${trackLiveData.value}")
        }
    }


}
