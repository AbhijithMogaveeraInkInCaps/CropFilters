package com.abhijith.videoaspectration

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import kotlin.random.Random

class MySimpleExoPlayer : PlayerView {

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs)

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    var isThereAnyNeedToReInit: Boolean = true

    init {
        keepScreenOn = true
    }

    private fun buildMediaSourceNew(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "myapplication"))
        return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun init(uri: Uri) {
        synchronized(this) {
            val loadControl = DefaultLoadControl()
            val bandwidthMeter = DefaultBandwidthMeter()
            val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
            player = simpleExoPlayer
            setUri(uri)
            simpleExoPlayer.addListener(object : Player.EventListener {
                override fun onTimelineChanged(
                    timeline: Timeline?,
                    manifest: Any?,
                    reason: Int
                ) {

                }

                override fun onTracksChanged(
                    trackGroups: TrackGroupArray?,
                    trackSelections: TrackSelectionArray?
                ) {

                }

                override fun onLoadingChanged(isLoading: Boolean) {

                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                }

                override fun onPlayerError(error: ExoPlaybackException?) {
                    freeMemory()
                }

                override fun onPositionDiscontinuity(reason: Int) {
                }

                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                }

                override fun onSeekProcessed() {
                }
            })
        }
    }

    fun freeMemory() {
        synchronized(this) {
            if (!isThereAnyNeedToReInit) {
                simpleExoPlayer.stop()
                simpleExoPlayer.release()
                player = null
                isThereAnyNeedToReInit = true
            }
        }
    }

    private fun setUri(uri: Uri) {
        synchronized(this) {
            val num = Random.nextInt(0, 4)
            val mediaSource = buildMediaSourceNew(uri)
            simpleExoPlayer.playWhenReady = false
            simpleExoPlayer.prepare(mediaSource)
        }
    }

    fun abort(uri: Uri) {
        synchronized(this) {
            if (!isThereAnyNeedToReInit) {
                simpleExoPlayer.playWhenReady = false
                simpleExoPlayer.playbackState
            }
        }
    }

    fun play(uri: Uri) {
        synchronized(this) {
            if (isThereAnyNeedToReInit) {
                isThereAnyNeedToReInit = false
                init(uri)
            }
            simpleExoPlayer.playWhenReady = true
            simpleExoPlayer.playbackState
            if (PlayerFlags.isMute)
                mute()
        }
    }


    fun pause(uri: Uri) {
        abort(uri)
    }

    fun mute() {
        if (!PlayerFlags.isMute) {
            simpleExoPlayer.volume = 1f
        } else {
            simpleExoPlayer.volume = 0f
        }
    }

    fun allocateMemoryAndBeReady(uri: Uri) {
        if (isThereAnyNeedToReInit) {
            isThereAnyNeedToReInit = false
            init(uri)
            simpleExoPlayer.seekTo(1)
        }
    }

    fun prepare(clippingMediaSource: ClippingMediaSource) {
        simpleExoPlayer.prepare(clippingMediaSource)
    }
}

object PlayerFlags {
    var isMute = false
}