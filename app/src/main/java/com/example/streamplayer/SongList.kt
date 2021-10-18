package com.example.streamplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamplayer.model.ArtistImages
import com.example.streamplayer.model.ImagesItem
import com.example.streamplayer.model.TopList
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.service.ApiService
import com.example.streamplayer.service.ArtistImageApiService
import retrofit2.Call
import retrofit2.Response
import java.util.*


class SongList : Fragment()  {

        private var mApiService: ApiService? = null
        private var topListAdapter: TrackListAdapter? = null


       var tracks = mutableListOf<Tracks>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topListAdapter = TrackListAdapter(requireContext(), tracks as ArrayList<Tracks>)
        val rv: RecyclerView = requireActivity().findViewById(R.id.list_tracks_rv)
        rv.layoutManager = LinearLayoutManager(requireActivity())
        rv.adapter = topListAdapter
        fetchTracks()

    }

       fun fetchTracks(): List<Tracks> {
        val retrofit = ApiService.getApiService()
        val call = retrofit.fetchTracks()

        call.enqueue(object: retrofit2.Callback <TopList<List<Tracks>>> {

            override fun onResponse (call: Call <TopList<List<Tracks>>>, response: Response<TopList<List<Tracks>>>) {
                val trackResponse = response.body()
                //  Log.d("MyLog", "${response.body()}")

                if (trackResponse == null){
                    topListAdapter!!.notifyDataSetChanged()
                }
                if (trackResponse != null) {
               //     tracks.stream().filter(List<Tracks>()).collect(Collectors.toList())
                    val resp: List<Tracks> = trackResponse.tracks as List<Tracks>
                    for (i in 0..resp.size-1){
                        val artistId = resp[i].artistId
                //        Log.d("MyLog", "Artist ID: ${artistId}")
              //          val imageList = ArtistImageApiService
                        val artistImage = fetchArtistImg(artistId.toString())
                        Log.d("MyLog", "${artistImage}")
                        resp[i].artistImageUri = artistImage
                        Log.d("MyLog", "Resp: ${resp[i].toString()}")
                        tracks.add(resp[i])
                    }
                 //   tracks.addAll(resp)

                    topListAdapter!!.notifyDataSetChanged()

                }
            }

            override fun onFailure (call : Call<TopList<List<Tracks>>>, t: Throwable){

                Log.d("MyLog", "Error: ${t.localizedMessage}")
            }
        })
        return tracks
    }
    var artistImage = "http://static.rhap.com/img/356x237/3/8/0/2/18962083_356x237.jpg"
    private fun fetchArtistImg(artistId: String): String {


        val retrofit = ArtistImageApiService.getApiService()
        val call = retrofit.fetchArtist(artistId)

        call.enqueue(object: retrofit2.Callback <ArtistImages<List<ImagesItem>>> {

            override fun onResponse (call: Call<ArtistImages<List<ImagesItem>>>,
                                     response: Response<ArtistImages<List<ImagesItem>>>) {
                val artistImageResponse = response.body()


                          if (artistImageResponse == null){
                              topListAdapter!!.notifyDataSetChanged()
                          }
                           if (artistImageResponse != null) {

                val resp: List<ImagesItem> = artistImageResponse?.images as List<ImagesItem>
                artistImage = resp[0].url.toString()
                               Log.d("MyLog", "artist url: ${artistImage}")
                topListAdapter!!.notifyDataSetChanged()

                         }
            }

            override fun onFailure (call : Call<ArtistImages<List<ImagesItem>>>, t: Throwable){

                Log.d("MyLog", "Error: ${t.localizedMessage}")
            }
        })
        Log.d("MyLog", "artist url: ${artistImage}")
        return artistImage
    }






}


