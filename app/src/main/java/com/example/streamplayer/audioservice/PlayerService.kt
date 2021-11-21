package com.example.streamplayer.audioservice

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.example.streamplayer.RepositoryInstance
import com.example.streamplayer.UI.SongItemFragment
import com.example.streamplayer.model.Tracks
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File

class PlayerService() : Service() {
    private val NOTIFICATION_ID = 404
    private val NOTIFICATION_DEFAULT_CHANNEL_ID = "default_channel"
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private val stateBuilder = PlaybackStateCompat.Builder().setActions(
        PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    )
    private var mediaSession: MediaSessionCompat? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var audioFocusRequested = false
    private var exoPlayer: SimpleExoPlayer? = null
    private var extractorsFactory: ExtractorsFactory? = null
    private var dataSourceFactory: DataSource.Factory? = null
    val musicRepository = RepositoryInstance.getMusicRepository()


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            createNotificationChannel()

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .setAcceptsDelayedFocusGain(false)
                .setWillPauseWhenDucked(true)
                .setAudioAttributes(audioAttributes)
                .build()
        }
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mediaSession = MediaSessionCompat(this, "PlayerService")
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession!!.setCallback(mediaSessionCallback)
        val appContext = applicationContext
        val activityIntent = Intent(appContext, SongItemFragment::class.java)
        mediaSession!!.setSessionActivity(
            PendingIntent.getActivity(
                appContext,
                0,
                activityIntent,
                0
            )
        )
        val mediaButtonIntent = Intent(
            Intent.ACTION_MEDIA_BUTTON, null, appContext,
            MediaButtonReceiver::class.java
        )
        mediaSession!!.setMediaButtonReceiver(
            PendingIntent.getBroadcast(
                appContext,
                0,
                mediaButtonIntent,
                0
            )
        )

        exoPlayer = SimpleExoPlayer.Builder(this).build()
        exoPlayer!!.addListener(exoPlayerListener)

        //********** cash add *******************
        val httpDataSourceFactory: DataSource.Factory = OkHttpDataSource.Factory(
            OkHttpClient()
        )

        val cache: Cache = SimpleCache(
            File(this.cacheDir.absolutePath + "/exoplayer"), LeastRecentlyUsedCacheEvictor(
                (1024 * 1024 * 100).toLong()
            )
        ) // 100 Mb max
        dataSourceFactory = CacheDataSourceFactory(
            cache,
            httpDataSourceFactory,
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )
        extractorsFactory = DefaultExtractorsFactory()
    }
    // **********************************************


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession!!.release()
        exoPlayer!!.release()
    }

    private val mediaSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            private var currentUri: Uri? = null
            var currentState = PlaybackStateCompat.STATE_STOPPED





            override fun onPlay() {

                if (!exoPlayer!!.playWhenReady) {
                    startService(Intent(applicationContext, PlayerService::class.java))
                    if (musicRepository != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val track = getTrack()
                            if (track != null) {
                                updateMetadataFromTrack(track)
                                prepareToPlay(Uri.parse(track?.previewURL))
                                Log.d("MyLog", "Track in servise: $track")
                        }

                        }
                    }

                }


                //                      updateMetadataFromTrack(track)


                if (!audioFocusRequested) {
                    audioFocusRequested = true
                    val audioFocusResult: Int
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        audioFocusResult =
                            audioManager!!.requestAudioFocus((audioFocusRequest)!!)
                    } else {
                        audioFocusResult = audioManager!!.requestAudioFocus(
                            audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN
                        )
                    }
                    if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) return
                }
                mediaSession!!.isActive = true // Сразу после получения фокуса
                registerReceiver(
                    becomingNoisyReceiver,
                    IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                )
                exoPlayer!!.playWhenReady = true

                mediaSession!!.setPlaybackState(
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                        1f
                    ).build()
                )
                exoPlayer!!.playWhenReady = true
                currentState = PlaybackStateCompat.STATE_PLAYING
                refreshNotificationAndForegroundStatus(currentState)
            }

            override fun onPause() {
                if (exoPlayer!!.playWhenReady) {
                    exoPlayer!!.playWhenReady = false
                    unregisterReceiver(becomingNoisyReceiver)
                }
                mediaSession!!.setPlaybackState(
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PAUSED,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                        1f
                    ).build()
                )
                currentState = PlaybackStateCompat.STATE_PAUSED
                refreshNotificationAndForegroundStatus(currentState)
            }

            override fun onStop() {
                if (exoPlayer!!.playWhenReady) {
                    exoPlayer!!.playWhenReady = false
                    unregisterReceiver(becomingNoisyReceiver)
                }
                if (audioFocusRequested) {
                    audioFocusRequested = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        audioManager!!.abandonAudioFocusRequest((audioFocusRequest)!!)
                    } else {
                        audioManager!!.abandonAudioFocus(audioFocusChangeListener)
                    }
                }
                mediaSession!!.isActive = false
                mediaSession!!.setPlaybackState(
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_STOPPED,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                        1f
                    ).build()
                )
                currentState = PlaybackStateCompat.ACTION_STOP.toInt()
                refreshNotificationAndForegroundStatus(currentState)
                stopSelf()
            }

            override fun onSkipToNext() {
                if (musicRepository != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val track = getTrack()
                        if (track != null) {
                            updateMetadataFromTrack(track)
                        }
                        refreshNotificationAndForegroundStatus(currentState)
                        prepareToPlay(Uri.parse(track?.previewURL))


                    }



                    //                       track = musicRepository.getNext()
                }

                //                   updateMetadataFromTrack(track)

                //                   refreshNotificationAndForegroundStatus(currentState)
                CoroutineScope(Dispatchers.Main).launch {
                    //                       prepareToPlay(Uri.parse(track.previewURL))
                }

            }

            override fun onSkipToPrevious() {

                if (musicRepository != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val track = getTrack()
                        if (track != null) {
                            updateMetadataFromTrack(track)
                        }
                        refreshNotificationAndForegroundStatus(currentState)
                        prepareToPlay(Uri.parse(track?.previewURL))
                    }

                }
                //                        track = musicRepository.getPrevious()


                //                   updateMetadataFromTrack(track)
                //                   refreshNotificationAndForegroundStatus(currentState)

                    //                       prepareToPlay(Uri.parse(track.previewURL))

            }


            private fun prepareToPlay(uri: Uri) {
                if (uri != currentUri) {
                    currentUri = uri
                    val mediaSource =
                        dataSourceFactory?.let {
                            ProgressiveMediaSource.Factory(it)
                                .createMediaSource(currentUri!!)
                        }
                    if (mediaSource != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            exoPlayer!!.prepare(mediaSource)
                        }
                    }
                }
            }
        }

    private fun updateMetadataFromTrack(track: Tracks) {

        CoroutineScope(Dispatchers.Main).launch {
            metadataBuilder.putBitmap(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                track.artistImageUri?.let { imageToBitmap(it, this@PlayerService) }
            )
        }
        //   Log.d("MyLog", "name: ${track.name}, album: ${track.albumName}, artistName: ${track.artistName}")
        //              metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.name)
        metadataBuilder.putString(
            MediaMetadataCompat.METADATA_KEY_TITLE,
            track.name + "  (album: " + track.albumName + ")"
        )
        //        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.albumName)
//                metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artistName)
//                metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DURATION, "30")
        mediaSession?.setMetadata(metadataBuilder.build())
    }


    private val audioFocusChangeListener: OnAudioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> mediaSessionCallback.onPlay() // Не очень красиво
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaSessionCallback.onPause()
                else -> mediaSessionCallback.onPause()
            }
        }
    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Disconnecting headphones - stop playback
            if ((AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action)) {
                mediaSessionCallback.onPause()
            }
        }
    }
    private val exoPlayerListener: Player.EventListener = object : Player.EventListener {
        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
        }

        override fun onLoadingChanged(isLoading: Boolean) {
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playWhenReady && playbackState == ExoPlayer.STATE_ENDED) {
                mediaSessionCallback.onSkipToNext()
            }
        }

        fun onPlayerError(error: ExoPlaybackException?) {}
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    }

    override fun onBind(intent: Intent): IBinder? {
        return PlayerServiceBinder()
    }

    inner class PlayerServiceBinder() : Binder() {
        val mediaSessionToken: MediaSessionCompat.Token
            get() = mediaSession!!.sessionToken
    }

    private fun refreshNotificationAndForegroundStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                startForeground(NOTIFICATION_ID, getNotification(playbackState))
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                NotificationManagerCompat.from(this@PlayerService)
                    .notify(NOTIFICATION_ID, getNotification(playbackState))
                stopForeground(false)
            }
            else -> {
                stopForeground(true)
            }
        }
    }

    private fun getNotification(playbackState: Int): Notification {
        val builder: NotificationCompat.Builder? =
            mediaSession?.let { MediaHelper.helpFrom(this, it) }

        if (builder != null) apply {
            builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_previous,
                    "Previous",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )

            if (playbackState == PlaybackStateCompat.STATE_PLAYING) builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            ) else builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            )
            builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_next,
                    "Next",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )
            builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Stop",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
            )

            builder.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
                    .setMediaSession(mediaSession!!.sessionToken)
            ) // setMediaSession требуется для Android Wear
            builder.setSmallIcon(R.drawable.exo_icon_circular_play)
            builder.color = ContextCompat.getColor(
                this,
                R.color.exo_black_opacity_70
            ) // The whole background (in MediaStyle), not just icon background
            builder.setShowWhen(false)
            builder.priority = NotificationCompat.PRIORITY_HIGH
            builder.setOnlyAlertOnce(true)
            builder.setChannelId(NOTIFICATION_DEFAULT_CHANNEL_ID)
        }
        return builder!!.build()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(com.example.streamplayer.R.string.channel_name)
            val descriptionText = getString(com.example.streamplayer.R.string.channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val serviceChannel = NotificationChannel(
                NOTIFICATION_DEFAULT_CHANNEL_ID, name, importance
            )
            serviceChannel.description = descriptionText
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    suspend fun getTrack(): Tracks? {
        var track: Tracks? = null
        musicRepository?.positionFlow?.collect {
            track = it
        }
        return track

    }
}




fun imageToBitmap(artistImageUri: String, context: Context): Bitmap? {
    var bitmap: Bitmap? = null
    CoroutineScope(Dispatchers.IO).launch {
        bitmap =
            Glide.with(context)
                .asBitmap()
                .load(artistImageUri)
                //           .override(356, 237)
                .into(50, 50)
                .get()

    }
    return bitmap

}


