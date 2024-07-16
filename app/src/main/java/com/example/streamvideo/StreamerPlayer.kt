package com.example.streamvideo

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.example.streamvideo.viewModel.VideoPlayerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StreamerPlayer(
    onPlayerClose: (isVideoPlaying: Boolean) -> Unit,
    exoPlayerPlaying: Boolean,
    isPlaying: Boolean,
    viewModel: VideoPlayerViewModel
) {
    var showControls by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable {
                showControls = !showControls
                resetAutoHideControlsTimer(coroutineScope, showControls, { showControls = false })
            },
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            AndroidView(modifier = Modifier.fillMaxWidth(), factory = { cont ->
                PlayerView(cont).apply {
                    player = viewModel.exoPlayer
                    useController = false
                }
            })

            if (showControls) {
                IconButton(
                    onClick = {
                        onPlayerClose(false)
                        viewModel.releasePlayer()
                    }, modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        if (exoPlayerPlaying) viewModel.pause() else viewModel.play()
                        resetAutoHideControlsTimer(coroutineScope, showControls, { showControls = false })
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = if (exoPlayerPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = if (exoPlayerPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End) {
                    VideoProgressBar(viewModel.exoPlayer)
                }
            }


        } else {
            Image(painter = painterResource(id = R.drawable.loading), contentDescription = "Loading")
        }
    }
}

@Composable
fun VideoProgressBar(player: Player?) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(player) {
        while (true) {
            delay(100)
            progress = if (player?.duration ?: 0 > 0) {
                player?.currentPosition?.toFloat()!! / player.duration.toFloat()
            } else {
                0f
            }
        }
    }

    Column(verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End) {
        // Exibe o progresso como uma barra
       // LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())

        // Permite ao usuário buscar uma posição
        Slider(
            value = progress,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF004ECC),
                activeTrackColor =  Color(0xFF004ECC),
                inactiveTrackColor = Color(0xFFB1C2D5)
            ),
            onValueChange = { newValue ->
                val newPosition = (player?.duration ?: 0) * newValue
                player?.seekTo(newPosition.toLong())
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        )
    }
}

fun resetAutoHideControlsTimer(coroutineScope: CoroutineScope, showControls: Boolean, hideControlsAction: () -> Unit) {
    if (showControls) {
        coroutineScope.launch {
            delay(5000)
            hideControlsAction()
        }
    }
}