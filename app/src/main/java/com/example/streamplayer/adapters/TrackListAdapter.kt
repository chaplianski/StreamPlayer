package com.example.streamplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.streamplayer.R
import com.example.streamplayer.model.Tracks


class TrackListAdapter(
    tracks: List<Tracks>
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks: List<Tracks> = tracks



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return TrackViewHolder(v)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) = holder.onBind(tracks[position])

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<Tracks>) {
        tracks = list
        Log.d("MyLog", "listTracks $tracks")
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var itemSongArtist: TextView = itemView.findViewById(R.id.item_song_artist)
    var itemSongTile: TextView = itemView.findViewById(R.id.item_song_title)
    var itemTrack: CardView = itemView.findViewById(R.id.item_track)
    var itemSongItemAristImage: ImageView = itemView.findViewById(R.id.iv_song_item_artist_image)
    var itemSongChatNumber: TextView = itemView.findViewById(R.id.tv_item_number)

    fun onBind(trackItem: Tracks) {
        itemSongArtist.text = trackItem.artistName
        itemSongTile.text = trackItem.name
        itemTrack.setOnClickListener {
            //    Toast.makeText(holder.itemView.context,"Click", Toast.LENGTH_SHORT).show()

            val bundle = bundleOf("position" to position)
            Navigation.createNavigateOnClickListener(R.id.action_songList_to_songItem, bundle)
                .onClick(itemTrack)
        }
        itemSongChatNumber.text = trackItem.artistChatNumber.toString()

        //    val adapterContext = holder.itemView.context
        // val url = trackItem.artistImageUri

        val url = "http://static.rhap.com/img/356x237/3/8/0/2/18962083_356x237.jpg"
        Log.d("MyLog", "List images: ${url}")

        Glide.with(itemView.context).load(trackItem.artistImageUri)
            .error(R.drawable.ic_launcher_background)
            .override(200, 200)
            //.override(, Target.SIZE_ORIGINAL)
            .centerCrop()
            //        .placeholder(R.drawable.ic_avatar_dog)
            .into(itemSongItemAristImage)

    }


}