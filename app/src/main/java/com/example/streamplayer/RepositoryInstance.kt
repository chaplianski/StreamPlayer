package com.example.streamplayer

import android.app.Application
import android.content.Context
import com.example.streamplayer.audioservice.MusicRepository

class RepositoryInstance: Application() {




    override fun onCreate() {
        super.onCreate()
        var musicRepository = MusicRepository (applicationContext)
        mRepository = musicRepository

    }

    companion object{
        var mRepository: MusicRepository? = null

        fun getMusicRepository (): MusicRepository? {
            return mRepository
        }
    }
}