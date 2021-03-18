package com.abhijith.videoaspectration.aother

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.Formatter.formatShortFileSize
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.VideoView
import com.abhijith.videoaspectration.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lb.video_trimmer_library.BaseVideoTrimmerView
import com.lb.video_trimmer_library.view.RangeSeekBarView
import com.lb.video_trimmer_library.view.TimeLineView

class VideoTrimmerView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : BaseVideoTrimmerView(context, attrs, defStyleAttr) {

    private fun stringForTime(timeMs: Int): String {
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        val timeFormatter = java.util.Formatter()
        return if (hours > 0)
            timeFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        else
            timeFormatter.format("%02d:%02d", minutes, seconds).toString()
    }

    override fun initRootView() {
        LayoutInflater.from(context).inflate(R.layout.trimview_layout_naya, this, true)
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { initiateTrimming() }
    }

    override fun getTimeLineView(): TimeLineView = findViewById(R.id.timeLineView)

    override fun getTimeInfoContainer(): View = findViewById(R.id.timeTextContainer)

    override fun getPlayView(): View = findViewById(R.id.playIndicatorView)

    override fun getVideoView(): VideoView = findViewById(R.id.videoView)

    override fun getVideoViewContainer(): View = findViewById(R.id.videoViewContainer)

    override fun getRangeSeekBarView(): RangeSeekBarView = findViewById(R.id.rangeSeekBarView)

    @SuppressLint("SetTextI18n")
    override fun onRangeUpdated(startTimeInMs: Int, endTimeInMs: Int) {
        val seconds = context.getString(R.string.short_seconds)
        findViewById<TextView>(R.id.trimTimeRangeTextView).text = "${stringForTime(startTimeInMs)} $seconds - ${stringForTime(endTimeInMs)} $seconds"
    }

    @SuppressLint("SetTextI18n")
    override fun onVideoPlaybackReachingTime(timeInMs: Int) {
        val seconds = context.getString(R.string.short_seconds)
        findViewById<TextView>(R.id.playbackTimeTextView).text = "${stringForTime(timeInMs)} $seconds"
    }

    override fun onGotVideoFileSize(videoFileSize: Long) {
        findViewById<TextView>(R.id.videoFileSizeTextView).text = formatShortFileSize(context, videoFileSize)
    }
}