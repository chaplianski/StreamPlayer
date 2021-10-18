package com.example.streamplayer.service

import com.example.streamplayer.model.ArtistImages
import com.example.streamplayer.model.ImagesItem
import com.example.streamplayer.model.TopList
import com.example.streamplayer.model.Tracks
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/v2.2/tracks/top?apikey=NjEyMDFhMTktNjViNS00MjMyLWFlNzItMWI3YTUwNGMwNWJl")
 //   @Headers("api_key:NjEyMDFhMTktNjViNS00MjMyLWFlNzItMWI3YTUwNGMwNWJl")
 //   val api_key = "a0e438a13303912c905ba704375d08ef"
      fun fetchTracks(): Call <TopList<List<Tracks>>>

    companion object {
        val BASE_URL = "https://api.napster.com"

        fun getApiService(): ApiService {

            val url = BASE_URL
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okkHttpclient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }



 //   @GET ("/v2.2/artists/{artistId}/images?apikey=NjEyMDFhMTktNjViNS00MjMyLWFlNzItMWI3YTUwNGMwNWJl")
//    fun fetchArtist(@Path("artistId") artistId: String ): Call <ArtistImages<List<ImagesItem>>>
 //   companion object {

    //    private const val BASE_URL = "http://ws.audioscrobbler.com/2.0/"
    //    const val DEFAULT_PAGE_SIZE = 8
    //    const val MAX_PAGE_SIZE = 8

      /*  fun getApiService(): ApiService {
            val url = BASE_URL
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okkHttpclient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(ApiService::class.java)
        }*/
 //   }
}
