package com.example.streamplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.streamplayer.RepositoryInstance
import com.example.streamplayer.model.Tracks
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect


@InternalCoroutinesApi
 class SongItemViewModel(application: Application): AndroidViewModel(application) {

    //   var trackItemLiveData: MutableLiveData<List<Tracks>> = MutableLiveData()
    var trackLiveData: MutableLiveData<Tracks> = MutableLiveData()
    val repository = RepositoryInstance.getMusicRepository()

    //   val _trackItem = MutableStateFlow<Tracks>()
//    val trackItem: StateFlow<Tracks> = _trackItem

    init {
        //    CoroutineScope(Dispatchers.IO).launch {
        //        val track = repository?.getCurrent()
        //        trackLiveData.postValue(track)
        //       Log.d("MyLog", "sViewModel track: $track")

        /*        viewModelScope.launch {
                repository?.getCurrent()?.collect { track ->
                    trackLiveData.postValue(track)
                }
            }
*/
        /*    viewModelScope.launch {
            repository?.getCurrent()?.collect { track ->
                _trackItem.value(track)
            }
        }

        }*/
    }
}



