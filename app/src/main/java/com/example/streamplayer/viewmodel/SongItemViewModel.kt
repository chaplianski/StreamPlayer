package com.example.streamplayer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.streamplayer.MusicRepositoryInstance
import com.example.streamplayer.audioservice.MusicRepository
import com.example.streamplayer.model.Tracks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

 class SongItemViewModel(application: Application): AndroidViewModel(application) {
 //   class SongItemViewModel(): ViewModel() {

    var trackItemLiveData: MutableLiveData<List<Tracks>> = MutableLiveData()
    var trackLiveData: MutableLiveData<Tracks> = MutableLiveData()
  //  val repository = MusicRepository(application)

     val repository = MusicRepositoryInstance
    //private var playerService: PlayerService? = null
   //  val repo = playerService?.PlayerServiceBinder()?.getRepo()
     val positionViewModel = PositionViewModel

    init {
        CoroutineScope(Dispatchers.IO).launch {
        //   val track = repository.getCurrent()
            val track = MusicRepositoryInstance.getCurrent()
            trackLiveData.postValue(track)

           Log.d("MyLog", "sViewModel track: $track")
           Log.d("MyLog", "sViewModel trackLiveData.value: ${trackLiveData.value}")
        }
    }


}
