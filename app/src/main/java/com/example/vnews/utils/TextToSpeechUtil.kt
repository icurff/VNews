package com.example.vnews.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.vnews.ui.article.ArticleViewModel
import com.example.vnews.utils.MediaNotificationConstants.ACTION_STOP
import com.example.vnews.utils.MediaNotificationConstants.EXTRA_ARTICLE_TITLE
import com.example.vnews.utils.MediaNotificationConstants.EXTRA_CONTENT_TITLE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonNull.content
import java.util.Locale


class TextToSpeechUtil(
    private val context: Context,
    private val viewModel: ArticleViewModel? = null
) : MediaNotificationService.MediaControlCallback {
    private var textToSpeech: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)

    private val _isPaused = MutableStateFlow(false)

    private val _isInitialized = MutableStateFlow(false)

    private val _currentItemIndex = MutableStateFlow(-1)

    private val _speechRate = MutableStateFlow(1.0f)

    // Store contents of article
    private var contentItems = listOf<String>()

    private var mediaNotificationService: MediaNotificationService? = null
    
    // Flag to track service binding state
    private var isServiceBound = false

    // Service connection object
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaNotificationService.LocalBinder
            mediaNotificationService = binder.getService()
            mediaNotificationService?.setMediaControlCallback(this@TextToSpeechUtil)
            isServiceBound = true
            updateNotification()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaNotificationService = null
            isServiceBound = false
        }
    }

    companion object {
        const val MIN_SPEECH_RATE = 0.5f
        const val MAX_SPEECH_RATE = 2.0f
        const val SPEECH_RATE_STEP = 0.25f
    }

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.setLanguage(Locale("vi"))

                textToSpeech?.setSpeechRate(_speechRate.value)

                _isInitialized.value = true
            } else {
                _isInitialized.value = false
            }
        }

        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true

                syncStateWithViewModel()

                if (utteranceId?.startsWith("article_item_") == true) {
                    val indexStr = utteranceId.removePrefix("article_item_")
                    val index = indexStr.toIntOrNull() ?: -1
                    _currentItemIndex.value = index
                    viewModel?.updateTtsItemIndex(index)
                }
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId?.startsWith("article_item_") == true) {

                    val indexStr = utteranceId.removePrefix("article_item_")
                    val index = indexStr.toIntOrNull() ?: -1
                    if (index >= 0 && index < contentItems.size - 1) {
                        playContentItem(index + 1)
                    } else {
                        clearStates()
                    }
                }
            }

            override fun onError(utteranceId: String?) {
                clearStates()
            }

        })
    }

    private fun clearStates() {
        _isSpeaking.value = false
        _isPaused.value = false
        _currentItemIndex.value = -1

        syncStateWithViewModel()
    }

    private fun syncStateWithViewModel() {
        viewModel?.updateTtsState(
            isSpeaking = _isSpeaking.value,
            isPaused = _isPaused.value,
            currentItemIndex = _currentItemIndex.value,
            speechRate = _speechRate.value
        )

        updateNotification()
    }

    fun setContentItems(items: List<String>) {
        contentItems = items
    }


    private fun connectToMediaService() {
        if (!isServiceBound) {
            val intent = Intent(context, MediaNotificationService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun playContentItem(index: Int) {
        if (!_isInitialized.value || index < 0 || index >= contentItems.size) {
            return
        }

        textToSpeech?.stop()
        val text = contentItems[index]
        _currentItemIndex.value = index

        // Sync with ViewModel
        viewModel?.updateTtsItemIndex(index)

        connectToMediaService()

        // Start tts
        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "article_item_$index"
        )

        _isSpeaking.value = true
        _isPaused.value = false

        // Sync with ViewModel
        syncStateWithViewModel()

        startMediaNotificationService()
    }


    private fun startMediaNotificationService() {
        if (_isSpeaking.value && !_isPaused.value) {
            val articleTitle = viewModel?.selectedArticle?.value?.title ?: "VNews"
            val contentTitle =if (content.length > 30) content.substring(0, 30) + "..." else content

            val intent = Intent(context, MediaNotificationService::class.java)
            intent.putExtra(EXTRA_ARTICLE_TITLE, articleTitle)
            intent.putExtra(EXTRA_CONTENT_TITLE, contentTitle)
            context.startService(intent)
        }
    }

    fun playNextItem() {
        val currentIndex = _currentItemIndex.value
        if (currentIndex >= 0 && currentIndex < contentItems.size - 1) {
            playContentItem(currentIndex + 1)
        }
    }

    fun playPreviousItem() {
        val currentIndex = _currentItemIndex.value
        if (currentIndex > 0) {
            playContentItem(currentIndex - 1)
        }
    }


    fun setSpeechRate(rate: Float) {
        if (rate < MIN_SPEECH_RATE || rate > MAX_SPEECH_RATE) return

        _speechRate.value = rate
        textToSpeech?.setSpeechRate(rate)

        viewModel?.updateTtsSpeechRate(rate)

    }

    fun pause() {

        if (!_isSpeaking.value || _isPaused.value) return

        textToSpeech?.stop()

        _isPaused.value = true
        _isSpeaking.value = false

        syncStateWithViewModel()

    }

    fun resume() {
        if (!_isPaused.value || _currentItemIndex.value < 0) return
        connectToMediaService()
        val currentIndex = _currentItemIndex.value
        val text = contentItems[currentIndex]

        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "article_item_$currentIndex"
        )
        _isSpeaking.value = true
        _isPaused.value = false

        syncStateWithViewModel()

    }

    fun stop() {
        textToSpeech?.stop()

        _currentItemIndex.value = -1
        _isSpeaking.value = false
        _isPaused.value = false


        viewModel?.updateTtsItemIndex(-1)
        viewModel?.updateTtsState(
            isSpeaking = false,
            isPaused = false,
            currentItemIndex = -1,
            speechRate = _speechRate.value
        )

        removeNotification()
    }

    private fun removeNotification() {
        val intent = Intent(context, MediaNotificationService::class.java)
        intent.action = ACTION_STOP
        context.startService(intent)

        if (isServiceBound) {
            try {
                context.unbindService(serviceConnection)
                isServiceBound = false
            } catch (e: IllegalArgumentException) {
                isServiceBound = false
            }
        }
        
        mediaNotificationService = null
    }


    fun shutdown() {
        stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    // Notification Control Functions
    override fun onPlay() {
        if (_isPaused.value) {
            resume()
        } else if (_currentItemIndex.value >= 0) {
            playContentItem(_currentItemIndex.value)
        } else {
            playContentItem(0)
        }
    }

    override fun onPause() {
        pause()
    }

    override fun onPrevious() {
        playPreviousItem()
    }

    override fun onNext() {
        playNextItem()
    }

    override fun onStop() {
        stop()
    }

    private fun updateNotification() {
        if (mediaNotificationService == null) return
        
        // Ensure we have content items before accessing them
        if (contentItems.isEmpty()) return
        
        val articleTitle = contentItems.firstOrNull() ?: ""
        val currentContentItem = if (_currentItemIndex.value >= 0 && _currentItemIndex.value < contentItems.size) {
            contentItems[_currentItemIndex.value]
        } else ""
        
        val contentTitle = if (currentContentItem.length > 30) 
            currentContentItem.substring(0, 30) + "..." 
        else 
            currentContentItem
            
        val thumbnailUrl = viewModel?.selectedArticle?.value?.thumbnail
        mediaNotificationService?.updateMediaInfo(articleTitle, contentTitle, thumbnailUrl)

        mediaNotificationService?.updatePlaybackState(
            isPlaying = _isSpeaking.value
        )
    }
}
