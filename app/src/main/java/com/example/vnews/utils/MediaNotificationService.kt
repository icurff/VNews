package com.example.vnews.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.LruCache
import androidx.core.app.NotificationCompat
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.vnews.MainActivity
import com.example.vnews.R
import java.net.URL
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

@UnstableApi
class MediaNotificationService : Service() {

    companion object {
        private const val TAG = "MediaNotificationService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "vnews_media_channel"
        private const val CHANNEL_NAME = "VNews Media Player"

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

    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var notificationManager: NotificationManager
    private var isServiceForeground = false

    // Current media info
    private var articleTitle: String = ""
    private var contentTitle: String = ""
    private var playbackActive: Boolean = false
    private var mediaControlCallback: MediaControlCallback? = null


    private var thumbnailUrl: String? = null


    private val imageCache = LruCache<String, Bitmap>(10)
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    inner class LocalBinder : Binder() {
        fun getService(): MediaNotificationService = this@MediaNotificationService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // Initialize notification manager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        createNotificationChannel()

        // Initialize player and media session
        initializePlayer()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    private fun initializePlayer() {
        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build().apply {
            // Set up player listeners
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
                    Log.e(TAG, "Player error: ${error.message}")
                    playbackActive = false
                    updateNotification()
                }
            })
        }

        // Create media session with the player
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }

        // Return normal service mode (not sticky) so it doesn't restart automatically
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        // Extract data from intent if available
        intent.getStringExtra(EXTRA_ARTICLE_TITLE)?.let {
            articleTitle = it
        }

        intent.getStringExtra(EXTRA_CONTENT_TITLE)?.let {
            contentTitle = it
        }

        // Handle media control actions
        when (intent.action) {
            ACTION_PLAY -> {
                playbackActive = true
                mediaControlCallback?.onPlay()
                // Không cần cập nhật notification ở đây vì callback sẽ cập nhật
            }

            ACTION_PAUSE -> {
                // Cập nhật trạng thái pause
                playbackActive = false
                // Gọi callback để TextToSpeechUtil xử lý pause
                mediaControlCallback?.onPause()
                // Không cập nhật notification ở đây, nó sẽ được cập nhật từ TextToSpeechUtil
            }

            ACTION_PREV -> {
                mediaControlCallback?.onPrevious()
                // Không cần cập nhật notification ở đây
            }

            ACTION_NEXT -> {
                mediaControlCallback?.onNext()
                // Không cần cập nhật notification ở đây
            }

            ACTION_STOP -> {
                // Dừng playback
                playbackActive = false
                mediaControlCallback?.onStop()

                // Xóa hoàn toàn notification và dừng service
                removeNotificationCompletely()
            }
        }
    }

    // Function to completely remove notification and stop service
    private fun removeNotificationCompletely() {
            stopForeground(STOP_FOREGROUND_REMOVE)

            // Gọi callback để thông báo TextToSpeechUtil cập nhật trạng thái
            mediaControlCallback?.onStop()

            // Dừng service
            stopSelf()

    }

    fun updatePlaybackState(isPlaying: Boolean) {
        // Cập nhật trạng thái chơi nhạc dựa trên trạng thái TTS
        // isPlaying: true nếu đang phát, false nếu đã dừng hoặc pause
        this.playbackActive = isPlaying && !isPaused()

        // Update notification
        updateNotification()
    }

    // Thêm phương thức kiểm tra trạng thái pause
    private fun isPaused(): Boolean {
        // Kiểm tra xem callback có phải là TextToSpeechUtil không và gọi phương thức của nó
        val callback = mediaControlCallback
        if (callback != null && callback is TextToSpeechUtil) {
            return callback.isPaused()
        }
        return false
    }

    fun updateMediaInfo(articleTitle: String, contentTitle: String, thumbnailUrl: String? = null) {
        this.articleTitle = articleTitle
        this.contentTitle = contentTitle
        this.thumbnailUrl = thumbnailUrl

        // Update notification
        updateNotification()
    }

    fun setMediaControlCallback(callback: MediaControlCallback) {
        this.mediaControlCallback = callback
    }

    private fun updateNotification() {
        // Create a PendingIntent for launching the app when notification is tapped
        val contentIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Xác định trạng thái pause để hiển thị đúng icon
        val isPaused = isPaused()

        // Create action intents with unique request codes to avoid PendingIntent collision
        val playPauseIntent = Intent(this, MediaNotificationService::class.java).apply {
            // Nếu đang pause hoặc không active, hiển thị nút play, ngược lại hiển thị nút pause
            action = if (!playbackActive || isPaused) ACTION_PLAY else ACTION_PAUSE
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

        // Define the actions for the notification
        val actions = arrayOf(
            NotificationCompat.Action(
                R.drawable.ic_previous,
                "Previous",
                previousPendingIntent
            ),
            NotificationCompat.Action(
                if (!playbackActive || isPaused) R.drawable.ic_play else R.drawable.ic_pause,
                if (!playbackActive || isPaused) "Play" else "Pause",
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

        // Build the notification base without the large icon first
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

        // Check if we already have the bitmap in cache
        if (thumbnailUrl != null) {
            val cachedBitmap = imageCache.get(thumbnailUrl)
            if (cachedBitmap != null) {
                // Tối ưu hóa bitmap cho notification
                val optimizedBitmap = optimizeBitmapForNotification(cachedBitmap)
                // Use optimized bitmap
                builder.setLargeIcon(optimizedBitmap)
                showNotification(builder.build())
            } else {
                // Show notification immediately without thumbnail
                showNotification(builder.build())

                // Then load thumbnail asynchronously and update notification when ready
                loadThumbnailAsync(thumbnailUrl!!) { bitmap ->
                    if (bitmap != null) {
                        // Tối ưu hóa bitmap cho notification
                        val optimizedBitmap = optimizeBitmapForNotification(bitmap)
                        builder.setLargeIcon(optimizedBitmap)
                        showNotification(builder.build())
                    }
                }
            }
        } else {
            // No thumbnail, show notification as is
            showNotification(builder.build())
        }
    }

    private fun showNotification(notification: Notification) {
        // Update or create foreground notification
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

    private fun loadThumbnailAsync(url: String, callback: (Bitmap?) -> Unit) {
        executor.execute {
            try {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                // Yêu cầu chất lượng tối đa
                connection.setRequestProperty("Accept", "image/webp,image/*;q=1.0")
                connection.connect()

                val input = connection.getInputStream()

                // Đọc toàn bộ ảnh vào bộ nhớ trước để tránh lỗi stream
                val bytes = input.readBytes()
                input.close()

                // Đọc thông tin ảnh trước để xác định kích thước thực
                val optionsInfo = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, optionsInfo)

                // Cấu hình tùy chọn để đọc ảnh với chất lượng cao nhất
                val options = BitmapFactory.Options().apply {
                    // Sử dụng cấu hình ARGB_8888 cho chất lượng tốt nhất
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                    // Tắt tính năng tự động giảm chất lượng
                    inScaled = false
                    // Giảm thiểu nén
                    inSampleSize = 1
                    // Đảm bảo đọc toàn bộ pixel
                    inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
                    inDensity = DisplayMetrics.DENSITY_DEFAULT
                    // Đọc ảnh ở chất lượng gốc
                    inJustDecodeBounds = false
                }

                // Đọc lại ảnh với cấu hình chất lượng cao
                val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

                // Xử lý ảnh với chất lượng cao
                val processedBitmap = if (originalBitmap != null) {
                    // Kích thước thích hợp cho notification (lớn hơn để đảm bảo nét)
                    val targetSize = 512 // Tăng kích thước để đảm bảo nét

                    if (originalBitmap.width > targetSize || originalBitmap.height > targetSize) {
                        // Tính toán tỷ lệ giữ nguyên aspect ratio
                        val scale =
                            targetSize.toFloat() / max(originalBitmap.width, originalBitmap.height)
                        val width = (originalBitmap.width * scale).toInt()
                        val height = (originalBitmap.height * scale).toInt()

                        // Sử dụng phương thức tốt nhất để resize
                        Bitmap.createScaledBitmap(originalBitmap, width, height, true)
                    } else {
                        // Nếu ảnh đã nhỏ, sử dụng nguyên kích thước
                        originalBitmap
                    }
                } else {
                    null
                }

                // Cache ảnh
                if (processedBitmap != null) {
                    imageCache.put(url, processedBitmap)
                }

                // Trả về kết quả trên main thread
                mainHandler.post {
                    callback(processedBitmap)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading thumbnail: ${e.message}")
                mainHandler.post {
                    callback(null)
                }
            }
        }
    }

    private fun optimizeBitmapForNotification(bitmap: Bitmap): Bitmap {
            // Tính toán kích thước pixel thực tế cho notification dựa trên mật độ màn hình
            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density

            // Kích thước tối ưu cho notification icon (kích thước cao hơn để đảm bảo nét)
            val targetWidth = (128 * density).toInt()
            val targetHeight = (128 * density).toInt()

            // Nếu bitmap đã ở kích thước tốt, không cần resize
            if (bitmap.width == targetWidth && bitmap.height == targetHeight) {
                return bitmap
            }

            // Tính toán tỷ lệ giữ nguyên aspect ratio
            val widthRatio = targetWidth.toFloat() / bitmap.width
            val heightRatio = targetHeight.toFloat() / bitmap.height
            val ratio = max(widthRatio, heightRatio)

            val newWidth = (bitmap.width * ratio).toInt()
            val newHeight = (bitmap.height * ratio).toInt()

            // Tạo bitmap mới với chất lượng cao
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

            // Nếu bitmap đã được scale lên lớn hơn kích thước mục tiêu, cắt nó để phù hợp
            if (newWidth > targetWidth || newHeight > targetHeight) {
                val x = max(0, (newWidth - targetWidth) / 2)
                val y = max(0, (newHeight - targetHeight) / 2)

                val croppedBitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    x,
                    y,
                    min(targetWidth, newWidth),
                    min(targetHeight, newHeight)
                )

                // Nếu đã tạo bitmap mới, giải phóng bitmap cũ
                if (scaledBitmap != bitmap) {
                    scaledBitmap.recycle()
                }

                return croppedBitmap
            }

            return scaledBitmap

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        removeNotificationCompletely()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)

        // Giải phóng tài nguyên
        executor.shutdown()
        mediaSession.release()
        player.release()

        super.onDestroy()

    }

    // Interface for communication with the TextToSpeechUtil
    interface MediaControlCallback {
        fun onPlay()
        fun onPause()
        fun onPrevious()
        fun onNext()
        fun onStop()
    }
} 