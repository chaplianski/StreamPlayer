package com.example.streamplayer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.streamplayer.MainActivity
import com.example.streamplayer.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.Job


class PlayerNotificationService : Service() {

    private lateinit var mPlayer: SimpleExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
//private lateinit var playerNotificationManager: PlayerNotificationManager
    private var playerNotificationManager: NotificationManager? = null
    private var notificationId = 123;
    private var channelId = "channelId"
    private var isServiceStarted = false
    private var job: Job? = null
    var url = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.extras?.getString("currentTrack").toString()
        url = input
        Log.d("MyLog","Url: $input")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        Log.d("MyLog","Builder background service")
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Top List Songs")
            .setGroup("Player")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.exo_notification_small_icon)

            .build()
        startForeground(1, notification)
        Log.d("MyLog","Builder background service")

        //do heavy work on a background thread
        //stopSelf();
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY


 //       processCommand(intent)
//        return START_REDELIVER_INTENT
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        //   playerNotificationManager.setPlayer(null)
        mPlayer.release()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val serviceChannel = NotificationChannel(
                channelId, name, importance
            )
            serviceChannel.description = descriptionText
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }






    //removing service when user swipe out our app
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }





    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(applicationContext, MainActivity::class.java);
        return PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }




    /*   override fun onCreate() {
           super.onCreate()
           Log.d("MyLog","Lifecycle.onCreate()")
           notificationManager =
               applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
       }*/




    override fun onCreate() {
    super.onCreate()

    playerNotificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    mPlayer = SimpleExoPlayer.Builder(this).build()
    // Create a data source factory.
    dataSourceFactory = DefaultHttpDataSource.Factory()
  //  (Util.getUserAgent(context, "app-name"))
    mPlayer.playWhenReady = true
 //   mPlayer.prepare(getListOfMediaSource())

        val mediaItem: MediaItem = MediaItem.fromUri(url)





        mPlayer.setMediaItem(mediaItem)
        mPlayer.prepare()

    //attach player to playerNotificationManager
    object : PlayerNotificationManager.NotificationListener{
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            onGoing: Boolean) {
            startForeground(notificationId, notification)

        }
        override fun onNotificationCancelled(
            notificationId: Int,
            dismissedByUser: Boolean
        ) {
            stopSelf()
        }
    }
//   playerNotificationManager.setPlayer(mPlayer)
}




/*
    private fun commandStart(startTime: Long) {
        if (isServiceStarted) {
            return
        }
        Log.d("MyLog", "commandStart()")
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer(startTime)
            if (startTime == 0L) commandStop()

        } finally {
            isServiceStarted = true
        }
    }
*/

/*
    private fun continueTimer(startTime: Long) {
        job = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                playerNotificationManager?.notify(
                    notificationId,
                    getNotification(
                        ("Plaer play")
                    )
                )
                delay(INTERVAL)
            }
        }
    }
*/
    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        Log.d("MyLog", "commandStop()")
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("MyLog", "moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, PlayerNotificationService::class.java))
        } else {
            Log.d("MyLog", "moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, PlayerNotificationService::class.java))
        }
    }
/*
    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(notificationId, notification)

    }*/

  //  private fun getNotification(content: String) = builder.setContentText(content).build()

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "streemPlayer"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                channelId, channelName, importance
            )
            playerNotificationManager?.createNotificationChannel(notificationChannel)
        }
    }


/*
override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return START_STICKY
}
*/
// concatenatingMediaSource to pass media as a list,
// so that we can easily prev, next

    private fun getListOfMediaSource(): ConcatenatingMediaSource {
    val mediaUrlList = ArrayList<String>()
    mediaUrlList.add(url)
//    mediaUrlList.add("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
//    mediaUrlList.add("http://d3rlna7iyyu8wu.cloudfront.net/skip_armstrong/skip_armstrong_stereo_subs.m3u8")
//    mediaUrlList.add("https://moctobpltc-i.akamaihd.net/hls/live/571329/eight/playlist.m3u8")
//    mediaUrlList.add("https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8")

    val concatenatingMediaSource = ConcatenatingMediaSource()
    for (mediaUrl in mediaUrlList) {
        buildMediaSource(mediaUrl)?.let { concatenatingMediaSource.addMediaSource(it) }
    }

    return concatenatingMediaSource

}

//build media source to player
    private fun buildMediaSource(videoUrl: String): HlsMediaSource? {
    val uri = Uri.parse(videoUrl)
    // Create a HLS media source pointing to a playlist uri.
    return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
}


// detach player
private fun NotificationCompat.Builder.addAction(exoPlay: Int) {
    mPlayer.play()
}

    companion object {
        const val COMMAND_START = "COMMAND_START"
        const val COMMAND_STOP = "COMMAND_STOP"
        const val COMMAND_ID = "COMMAND_ID"
        const val STARTED_TIMER_TIME_MS = "STARTED_TIMER_TIME"
        private const val INTERVAL = 1000L
    }
}


