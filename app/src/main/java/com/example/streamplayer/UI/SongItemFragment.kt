package com.example.streamplayer.UI

import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.streamplayer.MusicRepositoryInstance
import com.example.streamplayer.R
import com.example.streamplayer.audioservice.MusicRepository
import com.example.streamplayer.audioservice.PlayerService
import com.example.streamplayer.databinding.FragmentSongItemBinding
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.viewmodel.SongItemViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SongItemFragment : Fragment() {
    lateinit var binding: FragmentSongItemBinding
    var positionFromList = 0

    //   private val listSongViewModel: SongViewModel by activityViewModels()
//    private val songItemViewModel: SongItemViewModel by activityViewModels()
     //{ListViewModelFactury(requireActivity().application)}
    var trackList = emptyList<Tracks>()
    lateinit var track: Tracks
    val songItemViewModel: SongItemViewModel by viewModels()
    //   val viewModel: SongItemViewModel by activityViewModels()
    private var playerServiceBinder: PlayerService.PlayerServiceBinder? = null
    private var playerService: PlayerService? = null
    private var mediaController: MediaControllerCompat? = null
    //    private var callback: MediaControllerCompat.Callback? = null
    private var serviceConnection: ServiceConnection? = null
   // val musicRepository = activity?.let { MusicRepository(it) }
    val musicRepository = MusicRepositoryInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songItemViewModel.trackLiveData.observe(this.viewLifecycleOwner, {
            track = it
            Log.d("MyLog", "track in songItemFragment: $track")
            fetchItemView(it)

        })



/*
        songItemViewModel.trackItemLiveData.observe(this.viewLifecycleOwner, {
            trackList = it
            fetchView(positionFromList)
            Log.d("MyLog", "tracks: $it")
        })

*/

        var statusButtom = "play"

        var callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                val playing = state.state == PlaybackStateCompat.STATE_PLAYING
                when (state.state){
                    1 -> {
                        binding.btStop.setEnabled(playing)
                        binding.btPlayPause.setImageResource(R.drawable.baseline_play_24)
                        statusButtom = "pause"
                    }
                    2 -> {
                        binding.btPlayPause.setImageResource(R.drawable.baseline_play_24)
                        statusButtom = "pause"
                    }
                    3 ->  {
                        binding.btPlayPause.setImageResource(R.drawable.baseline_pause_24)
                        statusButtom = "play"
                    }

                }


            }

        }

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                playerServiceBinder = service as PlayerService.PlayerServiceBinder

                try {
                    val token = playerServiceBinder?.mediaSessionToken
                    mediaController =
                        token?.let {
                            MediaControllerCompat(
                                activity,
                                it
                            )
                        }


                    mediaController?.registerCallback(callback as MediaControllerCompat.Callback)
                    mediaController?.playbackState?.let { callback.onPlaybackStateChanged(it) }
                //    (callback as MediaControllerCompat.Callback).onPlaybackStateChanged(mediaController?.playbackState)
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

        val intentPlayerService = Intent(activity, PlayerService::class.java)
        val executor: ExecutorService = Executors.newSingleThreadScheduledExecutor()
        serviceConnection?.let {
            activity?.bindService(
                intentPlayerService,
                it,
                Context.BIND_AUTO_CREATE,
//                executor,
                //               it
            )
        }

        //    var play = true

        binding.btPlayPause.setOnClickListener(View.OnClickListener {

            if (statusButtom.equals("pause")) {
                if (mediaController != null)
                mediaController!!.transportControls.play()
                statusButtom = "play"
            } else if (statusButtom.equals("play")) {
                if (mediaController != null)
                    mediaController!!.transportControls.pause()
            }
        })

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
            if (mediaController != null)
                mediaController!!.transportControls.stop()
            findNavController().navigate(R.id.action_songItem_to_songList)
        }

    }

    fun fetchView(positionFromList: Int) {
        if (trackList.size > positionFromList) {
            binding.tvArtistName.text = trackList.get(positionFromList).artistName
            binding.tvAlbumName.text = trackList.get(positionFromList).albumName
            binding.tvSongName.text = trackList.get(positionFromList).name
            binding.tvChatNumber.text = "Number in chat # ${positionFromList + 1}"

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
            //        mediaController!!.unregisterCallback(callback as MediaControllerCompat.Callback)
            mediaController = null
        }
        serviceConnection?.let { activity?.unbindService(it) }
    }


    fun fetchItemView(track: Tracks) {

        binding.tvArtistName.text = track.artistName
        binding.tvAlbumName.text = track.albumName
        binding.tvSongName.text = track.name
        binding.tvChatNumber.text = "Number in chat # ${track.artistChatNumber}"

        Glide.with(this).load(track.artistImageUri)
            .error(R.drawable.ic_launcher_background)
            .override(1800, 1800)
            //.override(, Target.SIZE_ORIGINAL)
            .centerCrop()
            //        .placeholder(R.drawable.ic_avatar_dog)
            .into(binding.imageView)

    }




}
