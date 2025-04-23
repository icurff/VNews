package com.example.vnews.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.vnews.MainActivity
import com.example.vnews.R
import com.example.vnews.utils.MediaNotificationConstants.ACTION_NEXT
import com.example.vnews.utils.MediaNotificationConstants.ACTION_PAUSE
import com.example.vnews.utils.MediaNotificationConstants.ACTION_PLAY
import com.example.vnews.utils.MediaNotificationConstants.ACTION_PREV
import com.example.vnews.utils.MediaNotificationConstants.ACTION_STOP
import com.example.vnews.utils.MediaNotificationConstants.CHANNEL_ID
import com.example.vnews.utils.MediaNotificationConstants.CHANNEL_NAME
import com.example.vnews.utils.MediaNotificationConstants.EXTRA_ARTICLE_TITLE
import com.example.vnews.utils.MediaNotificationConstants.EXTRA_CONTENT_TITLE
import com.example.vnews.utils.MediaNotificationConstants.NOTIFICATION_ID

object MediaNotificationConstants {
    const val NOTIFICATION_ID = 1001
    const val CHANNEL_ID = "vnews_media_channel"
    const val CHANNEL_NAME = "VNews Media Player"

    // Action keys
    const val ACTION_PLAY = "com.example.vnews.ACTION_PLAY"
    const val ACTION_PAUSE = "com.example.vnews.ACTION_PAUSE"
    const val ACTION_PREV = "com.example.vnews.ACTION_PREV"
    const val ACTION_NEXT = "com.example.vnews.ACTION_NEXT"
    const val ACTION_STOP = "com.example.vnews.ACTION_STOP"

    // Extra data keys
    const val EXTRA_ARTICLE_TITLE = "article_title"
    const val EXTRA_CONTENT_TITLE = "content_title"
}

class MediaNotificationService : Service() {
    private lateinit var mediaSession: MediaSession
    private lateinit var player: ExoPlayer
    private lateinit var notificationManager: NotificationManager

    private val binder = LocalBinder()
    private var isServiceForeground = false

    // Current media info
    private var articleTitle: String = ""
    private var contentTitle: String = ""
    private var playbackActive: Boolean = false
    private var mediaControlCallback: MediaControlCallback? = null
    private var thumbnailUrl: String? = null
    private var cachedBitmap: Bitmap? = null
    private var isLoadingThumbnail = false


    inner class LocalBinder : Binder() {
        fun getService(): MediaNotificationService = this@MediaNotificationService
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        initializePlayer()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "VNews media player controls"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            if (playbackActive) {
                                updateNotification()
                            }
                        }

                        Player.STATE_ENDED -> {
                            playbackActive = false
                            updateNotification()
                        }
                    }
                }

                override fun onIsPlayingChanged(isActuallyPlaying: Boolean) {
                    playbackActive = isActuallyPlaying
                    updateNotification()
                }

                override fun onPlayerError(error: PlaybackException) {
                    playbackActive = false
                    updateNotification()
                }
            })
        }

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        intent.getStringExtra(EXTRA_ARTICLE_TITLE)?.let {
            articleTitle = it
        }

        intent.getStringExtra(EXTRA_CONTENT_TITLE)?.let {
            contentTitle = it
        }

        when (intent.action) {
            ACTION_PLAY -> {
                playbackActive = true
                mediaControlCallback?.onPlay()
            }

            ACTION_PAUSE -> {
                playbackActive = false
                mediaControlCallback?.onPause()
            }

            ACTION_PREV -> {
                mediaControlCallback?.onPrevious()
            }

            ACTION_NEXT -> {
                mediaControlCallback?.onNext()
            }

            ACTION_STOP -> {
                playbackActive = false
                removeNotificationCompletely()
            }
        }
    }

    private fun removeNotificationCompletely() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        notificationManager.cancel(NOTIFICATION_ID)
        isServiceForeground = false
        mediaControlCallback?.onStop()
        stopSelf()
    }

    fun updatePlaybackState(isPlaying: Boolean) {
        this.playbackActive = isPlaying
        updateNotification()
    }

    fun updateMediaInfo(articleTitle: String, contentTitle: String, thumbnailUrl: String? = null) {
        this.articleTitle = articleTitle
        this.contentTitle = contentTitle

        // If thumbnail URL has changed, clear the cache and load new image
        if (this.thumbnailUrl != thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl
            cachedBitmap = null
            loadLargeIcon(thumbnailUrl)
        }

        updateNotification()
    }

    fun setMediaControlCallback(callback: MediaControlCallback) {
        this.mediaControlCallback = callback
    }

    @OptIn(UnstableApi::class)
    private fun updateNotification() {
        // Open app when notification is touched
        val contentIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
// ===============================================================================================
        // Action Intents
        val playPauseIntent = Intent(this, MediaNotificationService::class.java).apply {
            action = if (!playbackActive) ACTION_PLAY else ACTION_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, System.currentTimeMillis().toInt(), playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val previousIntent = Intent(this, MediaNotificationService::class.java).apply {
            action = ACTION_PREV
        }
        val previousPendingIntent = PendingIntent.getService(
            this, System.currentTimeMillis().toInt() + 1, previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, MediaNotificationService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            this, System.currentTimeMillis().toInt() + 2, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MediaNotificationService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, System.currentTimeMillis().toInt() + 3, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
// ===============================================================================================
        // Actions for the notification
        val actions = arrayOf(
            NotificationCompat.Action(
                R.drawable.ic_previous,
                "Previous",
                previousPendingIntent
            ),
            NotificationCompat.Action(
                if (!playbackActive) R.drawable.ic_play else R.drawable.ic_pause,
                if (!playbackActive) "Play" else "Pause",
                playPausePendingIntent
            ),
            NotificationCompat.Action(
                R.drawable.ic_next,
                "Next",
                nextPendingIntent
            ),
            NotificationCompat.Action(
                R.drawable.ic_close,
                "Stop",
                stopPendingIntent
            )
        )

        // Build the notification base
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(articleTitle)
            .setContentText(contentTitle)
            .setContentIntent(contentPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(actions[0]) // Previous
            .addAction(actions[1]) // Play/Pause
            .addAction(actions[2]) // Next
            .addAction(actions[3]) // Stop
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setOngoing(playbackActive)

        cachedBitmap?.let {
            builder.setLargeIcon(it)
        }
        if (cachedBitmap == null && !thumbnailUrl.isNullOrEmpty() && !isLoadingThumbnail) {
            loadLargeIcon(thumbnailUrl)
        }

        showNotification(builder.build())
    }

    // load large icon with glide
    private fun loadLargeIcon(thumbnailUrl: String?) {
        if (thumbnailUrl.isNullOrEmpty() || isLoadingThumbnail) return

        isLoadingThumbnail = true

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(512, 512)
            .centerCrop()

        Glide.with(applicationContext)
            .asBitmap()
            .load(thumbnailUrl)
            .apply(requestOptions)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    cachedBitmap = resource
                    isLoadingThumbnail = false
                    updateNotification()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    isLoadingThumbnail = false
                    cachedBitmap = null
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    isLoadingThumbnail = false
                    cachedBitmap = null
                }
            })
    }

    private fun showNotification(notification: Notification) {
        // remove the notification if we stop the playback
        if (!playbackActive && articleTitle.isEmpty() && contentTitle.isEmpty()) {
            removeNotificationCompletely()
            return
        }
        // show noti
        if (playbackActive && !isServiceForeground) {
            startForeground(NOTIFICATION_ID, notification)
            isServiceForeground = true
        } else if (!playbackActive && isServiceForeground) {
            stopForeground(STOP_FOREGROUND_DETACH)
            notificationManager.notify(NOTIFICATION_ID, notification)
            isServiceForeground = false
        } else {
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        removeNotificationCompletely()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        removeNotificationCompletely()
        mediaSession.release()
        player.release()
        cachedBitmap = null
        super.onDestroy()
    }

    interface MediaControlCallback {
        fun onPlay()
        fun onPause()
        fun onPrevious()
        fun onNext()
        fun onStop()
    }
} 