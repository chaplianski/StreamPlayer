package com.example.streamplayer.Services

import com.example.streamplayer.model.ArtistImages
import com.example.streamplayer.model.ImagesItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistImageApiService {
    @GET("/v2.2/artists/{artistId}/images?apikey=NjEyMDFhMTktNjViNS00MjMyLWFlNzItMWI3YTUwNGMwNWJl")
    fun fetchArtist(@Path("artistId") artistId: String ): Call<ArtistImages<List<ImagesItem>>>

    companion object {
        val BASE_URL = "https://api.napster.com"

        fun getApiService(): ArtistImageApiService {

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
            return retrofit.create(ArtistImageApiService::class.java)
        }
    }
}