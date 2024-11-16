package com.wioletamwrobel.wieluncityapp.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.media3.common.Player
import java.io.File

class AudioPlayerService () {

    private var audioPlayer: MediaPlayer? = null
    private var videoPlayer: MediaPlayer? = null

    fun play(file: Int, context: Context) {
        MediaPlayer.create(context, file).apply {
            audioPlayer = this
            start()
        }
    }

    fun stop() {
        audioPlayer?.stop()
        audioPlayer?.release()
        audioPlayer = null
    }
}