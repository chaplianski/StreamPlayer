package com.example.streamplayer.UI

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.streamplayer.R
import com.example.streamplayer.RepositoryInstance
import com.example.streamplayer.audioservice.PlayerService
import com.example.streamplayer.databinding.FragmentSongItemBinding
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.viewmodel.SongItemViewModel

class SongItemFragment : Fragment() {
    lateinit var binding: FragmentSongItemBinding
    private val songItemViewModel: SongItemViewModel by viewModels()
    //{ListViewModelFactury(requireActivity().application)}
    var trackList = emptyList<Tracks>()
    private var playerServiceBinder: PlayerService.PlayerServiceBinder? = null
    private var playerService: PlayerService? = null
    private var mediaController: MediaControllerCompat? = null
    val repository = RepositoryInstance.getMusicRepository()
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

        val buttonAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.click_button_animation)


        songItemViewModel.trackLiveData.observe(this.viewLifecycleOwner, {
            if (it != null) {
                fetchItemView(it)
            }

        })

        var statusButtom = "play"
        var callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                val playing = state.state == PlaybackStateCompat.STATE_PLAYING

                when (state.state) {
                    1 -> {
                        binding.btStop.setEnabled(!playing)
                        binding.btPlayPause.setImageResource(R.drawable.baseline_play_24)
                        statusButtom = "pause"
                    }
                    2 -> {
                        binding.btPlayPause.setImageResource(R.drawable.baseline_play_24)
                        statusButtom = "pause"
                    }
                    3 -> {
                        binding.btPlayPause.setImageResource(R.drawable.baseline_pause_24)
                        binding.btPlayPause.setEnabled(playing)
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
                    mediaController?.playbackState?.let {
                        callback.onPlaybackStateChanged(it)
                        //                       (callback as MediaControllerCompat.Callback).onPlaybackStateChanged(
                        //                           mediaController!!.playbackState)
                    }
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
 //       val executor: ExecutorService = Executors.newSingleThreadScheduledExecutor()
        serviceConnection?.let {
            activity?.bindService(
                intentPlayerService,
                it,
                Context.BIND_AUTO_CREATE,
            )
        }

        binding.btPlayPause.setOnClickListener(View.OnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.play()
            if (statusButtom.equals("pause")) {

                statusButtom = "play"
            } else if (statusButtom.equals("play")) {
                if (mediaController != null)
                    mediaController!!.transportControls.pause()
            }
            binding.btPlayPause.startAnimation(buttonAnimation)

        })

        binding.btStop.setOnClickListener(View.OnClickListener {

            if (mediaController != null)
                mediaController!!.transportControls.stop()
            binding.btStop.startAnimation(buttonAnimation)
        })

        binding.btNext.setOnClickListener(View.OnClickListener {

            if (mediaController != null)
                mediaController!!.transportControls.skipToNext()
            binding.btNext.startAnimation(buttonAnimation)
        })

        binding.btBack.setOnClickListener(View.OnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.skipToPrevious()
            binding.btBack.startAnimation(buttonAnimation)
        })

        binding.btToHightLevel.setOnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.stop()
            binding.btToHightLevel.startAnimation(buttonAnimation)
            activity?.onBackPressed()
        //  findNavController().navigate(R.id.action_songItem_to_songList)
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
            .override(800, 800)

            //.override(, Target.SIZE_ORIGINAL)
            .centerCrop()
            .circleCrop()
            //        .placeholder(R.drawable.ic_avatar_dog)
            .into(binding.imageView)
    }
    /******************** Вариант с запросом при наличии номера позиции ***************************
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
     */


}

