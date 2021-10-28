package com.example.streamplayer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.streamplayer.model.Tracks

@Database(entities = [Tracks::class],version = 1,exportSchema = false)
abstract class TrackDatabase: RoomDatabase() {
    abstract fun TrackDao(): TrackDao

    companion object{
        @Volatile
        private var INSTANCE: TrackDatabase? = null

        fun getDatabase (context: Context): TrackDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackDatabase::class.java,
                    "track_db"
                ).build()
                INSTANCE = instance
                return instance

            }
        }
    }
}