package com.example.streamplayer.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.streamplayer.model.Tracks
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query


@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: Tracks)

    @androidx.room.Query("SELECT * FROM track_table")
     fun getAll(): List<Tracks>

 //   @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(track: List<Tracks>)

    @androidx.room.Query ("DELETE FROM track_table")
    fun deleteAll ()

    @androidx.room.Query("SELECT * FROM track_table WHERE artistChatNumber LIKE :artistChatNumber")
 //   fun getTrackWithChatNumber(artistChatNumber: Int): Tracks
    fun getTrackWithChatNumber(artistChatNumber: Int): Flow<Tracks>

 //   @androidx.room.Query("SELECT COUNT(*) FROM track_table")
 //   suspend fun getTracksCount(): Int

 //   @androidx.room.Query("SELECT track FROM track_table")
 //   fun getTrack(): Tracks
}