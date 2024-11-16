package com.wioletamwrobel.wieluncityapp.player

import android.content.Context
import android.media.MediaPlayer
import java.io.File

class AudioPlayerService () {

    private var audioPlayer: MediaPlayer? = null

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