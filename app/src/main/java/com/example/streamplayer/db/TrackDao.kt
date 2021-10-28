package com.example.streamplayer.db

import androidx.room.*
import com.example.streamplayer.model.Tracks
import com.google.android.exoplayer2.extractor.mp4.Track
import retrofit2.http.GET
import retrofit2.http.Query

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: Tracks)

    @androidx.room.Query("SELECT * FROM track_table ORDER BY artistChatNumber")
    fun getAll(): List<Tracks>

 //   @Update
 //   fun updateDog (dog: Dog)

//    @Delete
//    fun deleteDog (dog: Dog)

 //   @androidx.room.Query("SELECT track FROM track_table")
 //   fun getTrack(): Tracks
}