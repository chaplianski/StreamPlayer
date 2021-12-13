package com.example.streamplayer.Presenter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
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
import com.example.streamplayer.Services.PlayerService
import com.example.streamplayer.databinding.FragmentSongItemBinding
import com.example.streamplayer.model.Tracks
import com.example.streamplayer.Viewmodels.SongItemViewModel
import com.google.android.exoplayer2.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SongItemFragment : Fragment() {
    lateinit var binding: FragmentSongItemBinding
    private val songItemViewModel: SongItemViewModel by viewModels()
    private var playerServiceBinder: PlayerService.PlayerServiceBinder? = null
    private var mediaController: MediaControllerCompat? = null
    private var serviceConnection: ServiceConnection? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    var flowMode: MutableStateFlow<Int?> = MutableStateFlow(null)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonAnimation: Animation =
            AnimationUtils.loadAnimation(context, R.anim.click_button_animation)

        songItemViewModel.trackLiveData.observe(this.viewLifecycleOwner, {
            if (it != null) {
                fetchItemView(it)
            }
        })

        var statusButtom = "play"

        val callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                val playing = state.state == PlaybackStateCompat.STATE_PLAYING

                when (state.state) {
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
        serviceConnection?.let {
            activity?.bindService(
                intentPlayerService,
                it,
                Context.BIND_AUTO_CREATE,
            )
        }

            var repeatState = 0
        binding.btRepeate.setOnClickListener{
            repeatState++
            if (repeatState > 1) {
                repeatState = 0
            }
            binding.btRepeate.startAnimation(buttonAnimation)
            if (repeatState == 1) binding.btRepeate.setImageResource(R.drawable.ic_baseline_cached_24_red)
            if (repeatState == 0) binding.btRepeate.setImageResource(R.drawable.ic_baseline_cached_24)
            mediaController?.transportControls?.stop()
        }

        binding.btPlayPause.setOnClickListener{
            if (mediaController != null)
                mediaController!!.transportControls.play()
            if (statusButtom.equals("pause")) {
                statusButtom = "play"
            } else if (statusButtom.equals("play")) {
                if (mediaController != null && repeatState == 1){
                    mediaController!!.transportControls.pause()
                }else {
                    mediaController!!.transportControls.pause()
                }

            }
            binding.btPlayPause.startAnimation(buttonAnimation)
        }

        binding.btNext.setOnClickListener {

            if (mediaController != null)
                mediaController!!.transportControls.skipToNext()
            binding.btNext.startAnimation(buttonAnimation)
        }

        binding.btBack.setOnClickListener{
            if (mediaController != null)
                mediaController!!.transportControls.skipToPrevious()
            binding.btBack.startAnimation(buttonAnimation)
        }

        binding.btToHightLevel.setOnClickListener {
            if (mediaController != null)
                mediaController!!.transportControls.stop()
            binding.btToHightLevel.startAnimation(buttonAnimation)
            activity?.onBackPressed()
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
            .centerCrop()
            .circleCrop()
            .into(binding.imageView)
    }
}

