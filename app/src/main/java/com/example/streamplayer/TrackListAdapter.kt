package com.example.streamplayer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.ListFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.streamplayer.model.ArtistImages
import com.example.streamplayer.model.ImagesItem
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.service.ArtistApiService
import retrofit2.Call
import retrofit2.Response


class TrackListAdapter(val tracks: ArrayList<Tracks>): RecyclerView.Adapter<TrackListAdapter.ViewHolder>() {

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
    /*    val trackItem: TrackItem = tracks[position]
        holder.itemSongArtist.text = trackItem.artist.toString()
        holder.itemSongTile.text = trackItem.name
        holder.itemSongRank.text = trackItem.attr.toString()
        holder.itemSongDuration.text = trackItem.duration.toString()
        val adapterContext = holder.itemView.context
        val url = trackItem.url
     */
        val trackItem: Tracks = tracks[position]
        holder.itemSongArtist.text = trackItem.artistName
        holder.itemSongTile.text = trackItem.name
      //  holder.itemTrack.setOnClickListener {
     //       val action = L
     //   }
     //   holder.itemSongAlbumName.text = trackItem.albumName.toString()
    //    holder.itemSongDuration.text = trackItem.previewURL
        val images = fetchArtistImg(trackItem.artistId.toString())
        Log.d("MyLog", "${trackItem.artistId.toString()}")
        Log.d("MyLog", "List images: ${images}")
//        val url = images[0].url

        val adapterContext = holder.itemView.context
     //   val url = trackItem.url

  //      Glide.with(adapterContext).load(url)
    //        .error(R.drawable.ic_avatar_dog)
  //          .centerCrop()
    //        .placeholder(R.drawable.ic_avatar_dog)
  //          .into(holder.itemSongItemAristImage)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
companion object{
    private fun fetchArtistImg(artistId: String): List<ImagesItem> {
        //    var artistIm = mutableListOf<ImagesItem>()
        var resp = mutableListOf<ImagesItem>()
        val retrofit = ArtistApiService.getApiService()
        val call = retrofit.fetchArtist(artistId)

        call.enqueue(object: retrofit2.Callback <ArtistImages<List<ImagesItem>>> {

            override fun onResponse (call: Call<ArtistImages<List<ImagesItem>>>,
                                     response: Response<ArtistImages<List<ImagesItem>>>) {
                val artistImageResponse = response.body()
                Log.d("MyLog", "Response body: ${response.body()}")

                //          if (artistImageResponse == null){
                //              topListAdapter!!.notifyDataSetChanged()
                //          }
                //           if (artistImageResponse != null) {
                //     tracks.stream().filter(List<Tracks>()).collect(Collectors.toList())
                val resp: List<ImagesItem> = artistImageResponse?.images as List<ImagesItem>
                //    artistIm.addAll(resp)
            //    Log.d("MyLog", "${resp}")
                //           topListAdapter!!.notifyDataSetChanged()

                //         }
            }

            override fun onFailure (call : Call<ArtistImages<List<ImagesItem>>>, t: Throwable){

                Log.d("MyLog", "Error: ${t.localizedMessage}")
            }
        })
        return resp //artistIm
    }
}

}