package com.example.streamplayer.itemsong

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.streamplayer.R
import com.example.streamplayer.audioservice.PlayerService
import com.example.streamplayer.databinding.FragmentSongItemBinding
import com.example.streamplayer.listsongs.SongViewModel
import com.example.streamplayer.model.Tracks
import com.google.android.exoplayer2.ExoPlayer


class SongItem : Fragment() {
    lateinit var binding: FragmentSongItemBinding
    lateinit var player: ExoPlayer
    var positionFromList = 0
    private val listSongViewModel: SongViewModel by activityViewModels()
    private val songItemViewModel: SongItemViewModel by activityViewModels()
    var trackList = emptyList<Tracks>()

    private var playerServiceBinder: PlayerService.PlayerServiceBinder? = null
    private var mediaController: MediaControllerCompat? = null
    private var callback: MediaControllerCompat.Callback? = null
    private var serviceConnection: ServiceConnection? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackList = listSongViewModel.trackListLiveData.value!!

        positionFromList = arguments?.getInt("position")!!
        fetchView(positionFromList)
   //     playingPlayer(positionFromList)

        Log.d("MyLog", "$positionFromList")
        Log.d("MyLog", "$trackList")

        var playerSeekBar = binding.playerSeekBar
        playerSeekBar.setMax(100)
        //    playerSeekBar.setProgress(player.contentPosition.toInt()/30000 * 100)

        callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                if (state == null) return
                val playing = state.state == PlaybackStateCompat.STATE_PLAYING
                binding.btPlayPause.setEnabled(!playing)
                //       binding.btPlayPause.setEnabled(playing)   // pause
                binding.btStop.setEnabled(playing)
            }
        }


        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                playerServiceBinder = service as PlayerService.PlayerServiceBinder
                try {
                    val token = mediaController!!.sessionToken
                    //    val token = MediaSession.Token
                    mediaController = MediaControllerCompat(
                        context,
                        token
                    )
                    mediaController!!.registerCallback(callback as MediaControllerCompat.Callback)
                    (callback as MediaControllerCompat.Callback).onPlaybackStateChanged(
                        mediaController!!.getPlaybackState()
                    )
                } catch (e: RemoteException) {
                    mediaController = null
                }

            }

            override fun onServiceDisconnected(name: ComponentName) {
                playerServiceBinder = null
                if (mediaController != null) {
                    mediaController!!.unregisterCallback(callback as MediaControllerCompat.Callback)
                    mediaController = null
                }
            }

        }



        bindService(
            Intent(this, PlayerService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )



        binding.btPlayPause.setOnClickListener(View.OnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.play()
        })

        //   pauseButton.setOnClickListener(View.OnClickListener { if (mediaController != null)
        //       mediaController!!.transportControls.pause() })

        binding.btStop.setOnClickListener(View.OnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.stop()
        })

        binding.btNext.setOnClickListener(View.OnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.skipToNext()
        })

        binding.btBack.setOnClickListener(View.OnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.skipToPrevious()
        })

        binding.btToHightLevel.setOnClickListener {
            findNavController().navigate(R.id.action_songItem_to_songList)
            player.release()
        }
/*
        binding.btPlayPause.setOnClickListener {

            if (player.isPlaying){
                    player.pause()
                    binding.btPlayPause.setImageResource(R.drawable.baseline_play_24)

                    Log.d("MyLog", "Play : ${player.isPlaying}")
                }else{
                    playingPlayer(positionFromList)
                    binding.btPlayPause.setImageResource(R.drawable.baseline_pause_24)
                    player.play()
                }


        }
        binding.btBack.setOnClickListener {
            if (positionFromList > 0) positionFromList--
            else positionFromList = trackList.size-1
                fetchView(positionFromList)
            if (player.isPlaying){
                player.release()
                playingPlayer(positionFromList)
                player.play()
            }

        }
        binding.btNext.setOnClickListener {
            if(positionFromList < trackList.size-1){positionFromList++}
            else positionFromList = 0
            fetchView(positionFromList)
            if (player.isPlaying) {
                player.release()
                playingPlayer(positionFromList)
                player.play()
            }

        }
        binding.btStop.setOnClickListener {
        //    Toast.makeText(context,"Stop",Toast.LENGTH_SHORT).show()
           player.release()
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            val intent = Intent(activity, PlayerService::class.java)
            intent.putExtra("currentTrack", trackList?.get(positionFromList)?.previewURL.toString())
        //    intent.putExtra("currentPosition", trackList?.get(positionFromList)?.previewURL.toString())
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                activity?.startForegroundService(intent)
            } else {
                activity?.startService(intent)
            }
        } catch (e: NoSuchElementException){
            Log.d("MyLog", e.toString())
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }




    fun playingPlayer(positionFromList: Int){
        val uri = trackList?.get(positionFromList)?.previewURL.toString()
        player = SimpleExoPlayer.Builder(requireContext()).build()
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
   //     player.play()

    }

    fun updateSeekBar (){
        if (player.isPlaying){

        }
    }*/
/*
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground(){
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

 */
    }
    fun fetchView (positionFromList: Int){
        if (trackList != null) {
            binding.tvArtistName.text = trackList.get(positionFromList).artistName
            binding.tvAlbumName.text = trackList.get(positionFromList).albumName
            binding.tvSongName.text = trackList.get(positionFromList).name
            binding.tvChatNumber.text = "Number in chat # ${positionFromList+1}"

            Glide.with(this).load(trackList.get(positionFromList).artistImageUri)
                .error(R.drawable.ic_launcher_background)
                .override(1800, 1800)
                //.override(, Target.SIZE_ORIGINAL)
                .centerCrop()
                //        .placeholder(R.drawable.ic_avatar_dog)
                .into(binding.imageView)

        }

    }
    override fun onDestroy() {
        super.onDestroy()
        playerServiceBinder = null
        if (mediaController != null) {
            mediaController!!.unregisterCallback(callback as MediaControllerCompat.Callback)
            mediaController = null
        }
    //    unbindService(serviceConnection)
    }
}