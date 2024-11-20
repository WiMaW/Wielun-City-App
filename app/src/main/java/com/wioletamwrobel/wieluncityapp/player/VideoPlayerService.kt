package com.wioletamwrobel.wieluncityapp.player

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.lang.Exception

class VideoPlayerService() {

    private var exoplayer: ExoPlayer? = null

    private fun preparePlayer(mediaItem: Int, context: Context) {
        try {
            exoplayer = ExoPlayer.Builder(context).build()
            exoplayer?.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/$mediaItem"))
            exoplayer?.prepare()
            exoplayer?.playWhenReady

        } catch (e: Exception) {
            Log.e("Exoplayer Error", e.message + " " + e.stackTraceToString())
        }
    }

    fun getPlayer(mediaItem: Int, context: Context): ExoPlayer? {
        preparePlayer(mediaItem, context)
        return exoplayer
    }

    fun clearPlayer() {
        exoplayer?.release()
        exoplayer = null
    }
}