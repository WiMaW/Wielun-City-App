package com.wioletamwrobel.wieluncityapp.player

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.lang.Exception

class VideoPlayerService(mediaItem: Uri, context: Context) {

    private var mediaService: MediaItem = MediaItem.fromUri(mediaItem)
    private var exoplayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private fun preparePlayer() {
        try {
            exoplayer.setMediaItem(mediaService)
            exoplayer.prepare()
        } catch (e: Exception) {
            Log.e("Exoplayer Error", e.message + " " + e.stackTraceToString())
        }
    }

    fun getPlayer(): ExoPlayer {
        preparePlayer()
        return exoplayer
    }
}