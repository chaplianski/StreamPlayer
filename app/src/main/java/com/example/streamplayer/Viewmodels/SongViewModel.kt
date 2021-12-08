package com.example.streamplayer.Viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.streamplayer.RepositoryInstance
import com.example.streamplayer.model.Tracks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SongViewModel(application: Application) : AndroidViewModel(application) {

    var trackListLiveData: MutableLiveData<List<Tracks>> = MutableLiveData()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = RepositoryInstance.getMusicRepository()
            trackListLiveData.postValue(repository?.fetch())
         }
    }

    companion object {
        const val MY_LOG = "MyLog"
    }

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
