package com.example.streamplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.streamplayer.databinding.FragmentSongItemBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

class SongItem : Fragment() {
    lateinit var binding: FragmentSongItemBinding
    lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btPlayPause.setOnClickListener {
            val uri = "https://freepd.com/music/Ice and Snow.mp3"
            player = SimpleExoPlayer.Builder(requireContext()).build()
            val mediaItem: MediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()


     //       Toast.makeText(context,"Play",Toast.LENGTH_SHORT).show()
        }
        binding.btBack.setOnClickListener {
            Toast.makeText(context,"Back",Toast.LENGTH_SHORT).show()
        }
        binding.btNext.setOnClickListener {
            Toast.makeText(context,"Next",Toast.LENGTH_SHORT).show()
        }
        binding.btToHightLevel.setOnClickListener {
            Toast.makeText(context,"To hight level",Toast.LENGTH_SHORT).show()
        }
        binding.btStop.setOnClickListener {
        //    Toast.makeText(context,"Stop",Toast.LENGTH_SHORT).show()
           player.release()
        }
        binding.btToHightLevel.setOnClickListener {
        //    Toast.makeText(context,"Stop",Toast.LENGTH_SHORT).show()
            findNavController().navigate(com.example.streamplayer.R.id.action_songItem_to_songList)
            player.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}