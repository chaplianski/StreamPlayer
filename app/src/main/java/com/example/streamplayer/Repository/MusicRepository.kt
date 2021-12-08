package com.example.streamplayer.Repository

import android.content.Context
import android.util.Log
import com.example.streamplayer.db.TrackDatabase
import com.example.streamplayer.model.ImagesItem
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.Services.ArtistImageApiService
import com.example.streamplayer.Services.TopListApiService
import com.example.streamplayer.Viewmodels.SongViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MusicRepository(val context: Context) {

     var tracks = mutableListOf<Tracks>()

    suspend fun fetch(): MutableList <Tracks> {

            val retrofit = TopListApiService.getApiService()
            val call = retrofit.fetchTracks()

            val response = call.execute()

            if (response.isSuccessful.not()) { // if failed call
               Log.d(SongViewModel.MY_LOG, response.message() + response.errorBody()?.toString())
        //        return@launch
            }

            val trackResponse = response.body()
            if (trackResponse == null) {
         //       _trackListLiveData.postValue(emptyList())
        //        return@launch
            } else {

                val resp: List<Tracks> = trackResponse.tracks as List<Tracks>

                for (i in resp.indices) {
                    val artistId = resp[i].artistId
                    val artistImage = fetchArtistImg(artistId.toString())
                    Log.d(SongViewModel.MY_LOG, "$artistImage")
                    resp[i].artistImageUri = artistImage
                    resp[i].artistChatNumber = i+1
                    tracks.add(resp[i])
                }
                val trackDatabase = TrackDatabase.getDatabase(context)
                trackDatabase.TrackDao().deleteAll()
                tracks.forEachIndexed {i, value -> trackDatabase.TrackDao().insertTrack(tracks[i])}
                return tracks

            }

            return tracks
    }

    private suspend fun fetchArtistImg(artistId: String): String {

        val retrofit = ArtistImageApiService.getApiService()
        val call = retrofit.fetchArtist(artistId)

        val response = call.execute()

        val artistImageResponse = response.body()

        if (artistImageResponse != null) {

            val resp: List<ImagesItem> = artistImageResponse?.images as List<ImagesItem>

            return resp[0].url.toString()

        }
        return ""
    }

 //**************************************

    suspend fun getTrackList(): List<Tracks> {
           val trackDatabase = TrackDatabase.getDatabase(context)
           val list = trackDatabase.TrackDao().getAll()

        return list
    }

//*********************************** Координация с навигацией плеера *********

 //   var currentItemIndex = PositionViewModel.getPosition()+1

    var currentItemIndex = 1

    fun getTrackPosition(position: Int): Int {
        Log.d("MyLog", "Position in Repository: $position")
        currentItemIndex = position+1
        return position
    }

    val maxIndex = 19


    suspend fun getNext() {

        if (currentItemIndex == maxIndex) currentItemIndex = 1 else currentItemIndex++
        Log.d("MyLog", "Position next in Repository: $currentItemIndex")
        getCurrent()
    }

    suspend fun getPrevious() {

        if (currentItemIndex == 1) currentItemIndex = maxIndex else currentItemIndex--
        Log.d("MyLog", "Position back in Repository: $currentItemIndex")
        getCurrent()
    }

    val positionFlow: MutableStateFlow<Tracks?> = MutableStateFlow(null)

    suspend fun updateTrack (){
        CoroutineScope(Dispatchers.IO).launch {
            getCurrent()
        }
    }

    suspend fun getCurrent() {
        val tracks = TrackDatabase.getDatabase(context).TrackDao().getTrackWithChatNumber(currentItemIndex)
        Log.d("MyLog", "tracks in Repository: $tracks")
        positionFlow.value = tracks
    }
}







