package com.example.streamplayer.audioservice

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.streamplayer.adapters.TrackListAdapter
import com.example.streamplayer.itemsong.SongItemFragment
import com.example.streamplayer.listsongs.SongViewModel
import com.example.streamplayer.model.ImagesItem
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.service.ApiService
import com.example.streamplayer.service.ArtistImageApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicRepository  {

    private val _trackListLiveData: MutableLiveData<List<Tracks>> = MutableLiveData()
    val trackListLiveData: LiveData<List<Tracks>>
        get() = _trackListLiveData
    var tracks = mutableListOf<Tracks>()
//    val trackListLiveData: LiveData<List<Tracks>> = fetch()



    fun fetch(): MutableLiveData<List <Tracks>> {
        CoroutineScope(Dispatchers.IO).launch() {
            val retrofit = ApiService.getApiService()
            val call = retrofit.fetchTracks()

            val response = call.execute()

            if (response.isSuccessful.not()) { // if failed call
                Log.d(SongViewModel.MY_LOG, response.message() + response.errorBody()?.toString())
                return@launch
            }

            val trackResponse = response.body()
            if (trackResponse == null) {
                _trackListLiveData.postValue(emptyList())
                return@launch
            } else {

                val resp: List<Tracks> = trackResponse.tracks as List<Tracks>

                for (i in resp.indices) {
                    val artistId = resp[i].artistId
                    val artistImage = fetchArtistImg(artistId.toString())
                    Log.d(SongViewModel.MY_LOG, "$artistImage")
                    resp[i].artistImageUri = artistImage
                    resp[i].artistChatNumber = i+1
                    Log.d(SongViewModel.MY_LOG, "Resp: ${resp[i]}")
                    tracks.add(resp[i])
                //    Log.d(SongViewModel.MY_LOG, "Tracks: ${tracks}")
                }
                _trackListLiveData.postValue(tracks)
            }
        }
    //    Log.d(SongViewModel.MY_LOG, "tracks: ${_trackListLiveData.toString()}")
    //    Log.d(SongViewModel.MY_LOG, "TrackListLiveData: ${trackListLiveData}")
        Log.d(SongViewModel.MY_LOG, "Tracks: ${tracks}")
        return _trackListLiveData

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

    //TODO-Andrey
    // размер листа треков, думаю можно получить из переменной-счетчика, но значение объекта-трека нужно извлекать из листа треков
    // я уже подумывал, что его тоже можно брать из вьюмодели, но как то выглядело бы странно


    private val maxIndex = 20
    private var currentItemIndex = 0

    fun getNext(): Tracks {
        if (currentItemIndex == maxIndex) currentItemIndex = 0 else currentItemIndex++
        return getCurrent()
    }

    fun getPrevious(): Tracks {
        if (currentItemIndex == 0) currentItemIndex = maxIndex else currentItemIndex--
        return getCurrent()
    }

    fun getCurrent(): Tracks {
        //TODO-Andrey
        return tracks [currentItemIndex]
    }


}


