package com.example.streamplayer.model

import com.squareup.moshi.Json

data class ArtistImages<T>(

	@Json(name="images")
	val images: List<ImagesItem?>? = null,

	@Json(name="meta")
	val meta: Meta? = null
)

data class ImagesItem(

	@Json(name="isDefault")
	val isDefault: Boolean? = null,

	@Json(name="contentId")
	val contentId: String? = null,

	@Json(name="width")
	val width: Int? = null,

	@Json(name="id")
	val id: String? = null,

	@Json(name="type")
	val type: String? = null,

	@Json(name="version")
	val version: Int? = null,

	@Json(name="imageType")
	val imageType: String? = null,

	@Json(name="url")
	val url: String? = null,

	@Json(name="height")
	val height: Int? = null
)

data class Meta(

	@Json(name="returnedCount")
	val returnedCount: Int? = null,

	@Json(name="totalCount")
	val totalCount: Any? = null
)
