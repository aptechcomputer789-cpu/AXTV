package com.axtv.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.axtv.app.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private var videoUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
        title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
    }

    override fun onStart() {
        super.onStart()
        if (player == null && videoUrl.isNotBlank()) {
            player = ExoPlayer.Builder(this).build().also { exoPlayer ->
                binding.playerView.player = exoPlayer
                exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    companion object {
        const val EXTRA_URL = "video_url"
        const val EXTRA_TITLE = "video_title"
    }
}
