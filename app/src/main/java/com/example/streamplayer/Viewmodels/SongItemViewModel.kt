package com.example.streamplayer.Viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.streamplayer.RepositoryInstance
import com.example.streamplayer.model.Tracks
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SongItemViewModel(application: Application): AndroidViewModel(application) {

    var trackLiveData: MutableLiveData<Tracks?> = MutableLiveData()
    val repository = RepositoryInstance.getMusicRepository()

    init {
        viewModelScope.launch {
            repository?.positionFlow?.collectLatest { track ->
                // if (track == null)
                repository.updateTrack()
                trackLiveData.postValue(track)
                Log.d("MyLog", "sViewModel track: $track")
            }

        }
    }
}



