package com.example.streamplayer.db

import androidx.room.*
import com.example.streamplayer.model.Tracks
import com.google.android.exoplayer2.extractor.mp4.Track
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE
import retrofit2.http.GET
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

 //   @androidx.room.Query("SELECT track FROM track_table")
 //   fun getTrack(): Tracks
}