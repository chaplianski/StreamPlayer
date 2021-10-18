package com.example.streamplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.streamplayer.databinding.FragmentSongItemBinding

class SongItem : Fragment() {
    lateinit var binding: FragmentSongItemBinding
    lateinit var mediaPlayer: MediaPlayer

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
             mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(uri)
                prepare()
                start()
            }
            Toast.makeText(context,"Play",Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context,"Stop",Toast.LENGTH_SHORT).show()
            mediaPlayer.reset()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }

}