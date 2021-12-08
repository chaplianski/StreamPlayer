package com.example.streamplayer.Services

/*
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

}
*/


