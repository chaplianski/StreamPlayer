package com.example.streamplayer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.streamplayer.model.ArtistImages
import com.example.streamplayer.model.ImagesItem
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.service.ArtistImageApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response


class TrackListAdapter(val adapterContext: Context, val tracks: ArrayList<Tracks>): RecyclerView.Adapter<TrackListAdapter.ViewHolder>() {

    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {

        var itemSongArtist: TextView = itemView.findViewById(R.id.item_song_artist)
        var itemSongTile: TextView = itemView.findViewById(R.id.item_song_title)
        var itemTrack: CardView = itemView.findViewById(R.id.item_track)
    //    var itemSongDuration: TextView = itemView.findViewById(R.id.item_song_duration)
    //    var itemSongAlbumName: TextView = itemView.findViewById(R.id.item_song_rank)
        var itemSongItemAristImage: ImageView = itemView.findViewById(R.id.iv_song_item_artist_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val  v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        val trackItem: Tracks = tracks[position]
        holder.itemSongArtist.text = trackItem.artistName
        holder.itemSongTile.text = trackItem.name
        holder.itemTrack.setOnClickListener {
        //    Toast.makeText(holder.itemView.context,"Click", Toast.LENGTH_SHORT).show()
            Navigation.createNavigateOnClickListener(R.id.action_songList_to_songItem).onClick(holder.itemTrack)
        }


    //    val adapterContext = holder.itemView.context
       // val url = trackItem.artistImageUri

        val url = "http://static.rhap.com/img/356x237/3/8/0/2/18962083_356x237.jpg"
        Log.d("MyLog", "List images: ${url}")


        Glide.with(adapterContext).load(url)
           //        .error(R.drawable.ic_avatar_dog)
         //   .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .centerCrop()
       //    .override(356, 237)
           //        .placeholder(R.drawable.ic_avatar_dog)
           .into(holder.itemSongItemAristImage)

   }


    override fun getItemCount(): Int {
        return tracks.size
    }


}