package com.example.streamplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamplayer.model.TopList
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.service.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

        topListAdapter = TrackListAdapter(tracks as ArrayList<Tracks>)
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
                    tracks.addAll(resp)
                    Log.d("MyLog", "${tracks}")
                    topListAdapter!!.notifyDataSetChanged()

                }
            }

            override fun onFailure (call : Call<TopList<List<Tracks>>>, t: Throwable){

                Log.d("MyLog", "Error: ${t.localizedMessage}")
            }
        })
        return tracks
    }







}


