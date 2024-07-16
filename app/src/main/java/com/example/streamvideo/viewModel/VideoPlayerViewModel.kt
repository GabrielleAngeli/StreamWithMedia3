package com.example.streamvideo.viewModel

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
import android.net.Uri
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import com.example.streamvideo.model.VideoData

class VideoPlayerViewModel: ViewModel() {
    var exoPlayer: ExoPlayer? = null
    var index: Int = 0
    var videoList: List<VideoData> = listOf()
    var exoplayerIsPlaying by mutableStateOf(false)

    fun initializePlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    fun releasePlayer() {
        exoPlayer?.playWhenReady = false
        exoPlayer?.release()
        exoPlayer = null
    }

    @OptIn(UnstableApi::class)
    fun playVideo() {
        exoPlayer?.let { player ->
            player.apply {
                stop()
                clearMediaItems()
                //setMediaItem(MediaItem.fromUri(Uri.parse(videoList[index].videoUrl)))
                // Cria um MediaSource para o vídeo M3U8
                val mediaSource = HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(videoList[index].videoUrl))

                // Prepara o player com o MediaSource
                setMediaSource(mediaSource)
                prepare()
                playWhenReady = true // Inicia a reprodução assim que o player estiver pronto
                prepare()
                play()
                exoplayerIsPlaying = true
            }
        }
    }

    fun play() {
        exoPlayer?.play()
        exoplayerIsPlaying = true
    }

    fun pause() {
        exoPlayer?.pause()
        exoplayerIsPlaying = false

    }

    @OptIn(UnstableApi::class)
    fun playerViewBuilder(context: Context): PlayerView {
        val activity = context as Activity
        val playerView = PlayerView(context).apply {
            player = exoPlayer
            controllerAutoShow = false
            useController = false
            keepScreenOn = true
            setFullscreenButtonClickListener { isFullScreen ->
                if (isFullScreen){
                    println("isFullScreen")
                    activity.requestedOrientation = SCREEN_ORIENTATION_USER_LANDSCAPE

                }else {
                    activity.requestedOrientation = SCREEN_ORIENTATION_USER

                }
            }
        }
        return playerView
    }
}