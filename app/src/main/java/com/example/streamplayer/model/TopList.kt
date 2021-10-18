package com.example.streamplayer.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class TopList<T>(

//	@Json(name="meta")
//	val meta: Meta? = null,

	@Json(name="tracks")
	val tracks: List<Tracks?>? = null
)

@Parcelize
data class Tracks(

	@Json(name="albumName")
	val albumName: String? = null,

	@Json(name="playbackSeconds")
	val playbackSeconds: Int? = null,

	@Json(name="previewURL")
	val previewURL: String? = null,

	@Json(name="index")
	val index: Int? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="artistName")
	val artistName: String? = null,

	@Json(name="artistId")
	val artistId: String? = null,

	@Json(name="artistImageUri")
	var artistImageUri: String? = null,


):Parcelable




