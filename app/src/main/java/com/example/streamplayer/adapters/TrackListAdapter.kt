package com.example.streamplayer.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.streamplayer.R
import com.example.streamplayer.model.Tracks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TrackListAdapter(
    tracks: List<Tracks>,
    val callback: PositionTransfer
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var tracks: List<Tracks> = tracks


    interface PositionTransfer {
        public fun onChangePosition(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)

        return TrackViewHolder(v, callback)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.onBind(tracks[position])
    //    holder.progressBarIndicator.visibility = View.GONE

    }


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

    class TrackViewHolder(itemView: View,
      val callback: TrackListAdapter.PositionTransfer) : RecyclerView.ViewHolder(itemView) {

    var itemSongArtist: TextView = itemView.findViewById(R.id.item_song_artist)
    var itemSongTile: TextView = itemView.findViewById(R.id.item_song_title)
    var itemTrack: CardView = itemView.findViewById(R.id.item_track)
    var itemSongItemAristImage: ImageView = itemView.findViewById(R.id.iv_song_item_artist_image)
    var itemSongChatNumber: TextView = itemView.findViewById(R.id.tv_item_number)



    fun onBind(trackItem: Tracks) {
        itemSongArtist.text = trackItem.artistName
        itemSongTile.text = trackItem.name
        itemTrack.setOnClickListener {
            callback.onChangePosition (bindingAdapterPosition)
            val bundle = bundleOf("position" to bindingAdapterPosition)
            Navigation.createNavigateOnClickListener(R.id.action_songList_to_songItem, bundle)
                .onClick(itemTrack)
        }
        itemSongChatNumber.text = trackItem.artistChatNumber.toString()

        Glide.with(itemView.context).load(trackItem.artistImageUri)
            .error(R.drawable.ic_launcher_background)
            .override(200, 200)
            .centerCrop()
            .into(itemSongItemAristImage)
    }
}
