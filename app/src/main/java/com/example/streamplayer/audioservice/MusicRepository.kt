package com.example.streamplayer.audioservice

import android.content.Context
import android.util.Log
import com.example.streamplayer.db.TrackDatabase
import com.example.streamplayer.model.ImagesItem
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.service.ArtistImageApiService
import com.example.streamplayer.service.TopListApiService
import com.example.streamplayer.viewmodel.PositionViewModel
import com.example.streamplayer.viewmodel.SongViewModel




class MusicRepository(val context: Context) {

 //   private val _trackListLiveData: MutableLiveData<List<Tracks>> = MutableLiveData()
//    val trackListLiveData: LiveData<List<Tracks>>
 //       get() = _trackListLiveData
    var tracks = mutableListOf<Tracks>()


//    val trackListLiveData: LiveData<List<Tracks>> = fetch()



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
    //                Log.d(SongViewModel.MY_LOG, "Resp: ${resp[i]}")
                    tracks.add(resp[i])
         //           val trackDatabase = TrackDatabase.getDatabase(context)
         //           trackDatabase.TrackDao().insertTrack(resp[i])

    //                Log.d(SongViewModel.MY_LOG, "track: ${tracks}")
                }
    //            Log.d(SongViewModel.MY_LOG, "2track: ${tracks}")
                val trackDatabase = TrackDatabase.getDatabase(context)
                trackDatabase.TrackDao().deleteAll()
                tracks.forEachIndexed {i, value -> trackDatabase.TrackDao().insertTrack(tracks[i])}
                return tracks

            }
            //    _trackListLiveData.postValue(tracks)

            return tracks //_trackListLiveData
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




 //   val trackList = tracks as List<Tracks>
//    val trackList = getNewTrackList()

/*
    private fun getNewTrackList(): List<Tracks> {
           val trackDatabase = TrackDatabase.getDatabase(context)
           val list = trackDatabase.TrackDao().getAll()
            Log.d("MyLog", "list: $list")
        return list
    }*/
    suspend fun getTrackList(): List<Tracks> {
           val trackDatabase = TrackDatabase.getDatabase(context)
           val list = trackDatabase.TrackDao().getAll()

        return list
    }

//***********************************


    var currentItemIndex = PositionViewModel.getPosition()+1

    val maxIndex = 20

    suspend fun getNext(): Tracks {
     Log.d("MyLog", "Position next in Repository: $currentItemIndex")
        if (currentItemIndex == maxIndex) currentItemIndex = 1 else currentItemIndex++
        return getCurrent()
    }

    suspend fun getPrevious(): Tracks {
        Log.d("MyLog", "Position back in Repository: $currentItemIndex")
        if (currentItemIndex == 1) currentItemIndex = maxIndex else currentItemIndex--
        return getCurrent()
    }

    suspend fun getCurrent(): Tracks {
        val trackDatabase = TrackDatabase.getDatabase(context)
        val list = trackDatabase.TrackDao().getTrackWithChatNumber(currentItemIndex)
        Log.d("MyLog", "Track from repository: $list")
        return list
        //return trackList[currentItemIndex]
    }

}


