package com.example.streamplayer.listsongs

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ListViewModelFactury(private val aplication: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            return SongViewModel(aplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }



    //    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      //      return modelClass.getConstructor(Application::class.java).newInstance(aplication)
    //    }
}