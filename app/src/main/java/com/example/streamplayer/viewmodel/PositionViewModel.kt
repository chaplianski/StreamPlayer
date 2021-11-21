package com.example.streamplayer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.streamplayer.audioservice.MusicRepository

class PositionViewModel(application: Application) : AndroidViewModel(application) {

    //    val trackPosition: MutableLiveData <Int> = MutableLiveData<Int>()
    //    val musicRepository = MusicRepository(getApplication())
    //   val position = trackPosition.value


    fun getTrackPosition(position: Int): Int {
        Log.d("MyLog", "Position in PositionViewModel: $position")
        pos = position
        return position
    }

    companion object {

        var pos = 0
        fun getPosition(): Int {
            Log.d("MyLog", "Pos in PositionViewModel: $pos")
            return pos
        }
    }
}

