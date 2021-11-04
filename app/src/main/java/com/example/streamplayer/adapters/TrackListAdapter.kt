package com.example.streamplayer.adapters

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.example.streamplayer.TrackPosition
import com.example.streamplayer.audioservice.MusicRepository
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

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), MusicRepository.GetTrackPosition {

    var itemSongArtist: TextView = itemView.findViewById(R.id.item_song_artist)
    var itemSongTile: TextView = itemView.findViewById(R.id.item_song_title)
    var itemTrack: CardView = itemView.findViewById(R.id.item_track)
    var itemSongItemAristImage: ImageView = itemView.findViewById(R.id.iv_song_item_artist_image)
    var itemSongChatNumber: TextView = itemView.findViewById(R.id.tv_item_number)

 //   interface PositionTransfer {
   //     public fun onSetPositionValues (position: Int): Int
 //   }

 //   var callback: PositionTransfer? = null

  //  fun registerCallBack(callback: PositionTransfer) {
 //       this.callback = callback
 //   }

 //   var trackPosition: TrackPosition? = null




    fun onBind(trackItem: Tracks) {
        itemSongArtist.text = trackItem.artistName
        itemSongTile.text = trackItem.name
        itemTrack.setOnClickListener {

         //   sendPosition (bindingAdapterPosition)
            val tViewHolder = TrackViewHolder(itemView)
            //     val musicRepository = MusicRepository(itemView.context)

            val musicRepository = MusicRepository(itemView.context)

        //    musicRepository.callback
        //    musicRepository.callback?.let { it1 -> musicRepository.registerCallBack(it1) }
            musicRepository.callback?.let { it1 -> musicRepository.getPosition(it1) }

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

    override fun onGetPositionValues(): Int {
        return bindingAdapterPosition
    }
    //   fun sendPosition (position: Int){
  //      callback?.onSetPositionValues(position)
 //   }



}
