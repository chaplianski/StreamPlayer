package com.example.streamplayer.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class TopList<T>(

//	@Json(name="meta")
//	val meta: Meta? = null,

	@Json(name="tracks")
	val tracks: List<Tracks?>? = null
)

@Entity(tableName = "track_table")
@Parcelize
data class Tracks(

	@PrimaryKey (autoGenerate = true)
	@ColumnInfo (name = "trackId")
	val trackId: Int? = null ,

	@Json(name="albumName")
	val albumName: String? ,

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

	@Json(name="artistChatNumber")
	var artistChatNumber: Int? = null,

	@Json(name="duration")
	var duration: String? = "30000",


):Parcelable



