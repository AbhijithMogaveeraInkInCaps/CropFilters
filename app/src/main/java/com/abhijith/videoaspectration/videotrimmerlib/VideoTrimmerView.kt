package com.abhijith.videoaspectration.videotrimmerlib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhijith.videoaspectration.R
import com.abhijith.videoaspectration.databinding.LayoutVideoTrimmerBinding
import com.abhijith.videoaspectration.videotrimmerlib.data.TrimmerDraft
import com.abhijith.videoaspectration.videotrimmerlib.slidingwindow.SlidingWindowView
import com.abhijith.videoaspectration.videotrimmerlib.tools.dpToPx
import com.abhijith.videoaspectration.videotrimmerlib.videoframe.VideoFramesAdaptor
import com.abhijith.videoaspectration.videotrimmerlib.videoframe.VideoFramesDecoration
import com.abhijith.videoaspectration.videotrimmerlib.videoframe.VideoFramesScrollListener
import java.io.File
import kotlin.math.roundToInt

class VideoTrimmerView @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle), VideoTrimmerContract.View {

    @DrawableRes
    private var leftBarRes: Int = R.drawable.trimmer_left_bar
    @DrawableRes
    private var rightBarRes: Int = R.drawable.trimmer_right_bar

    private var barWidth: Float = dpToPx(context, 10f)
    private var borderWidth: Float = 0f

    @ColorInt
    private var borderColor: Int = Color.BLACK
    @ColorInt
    private var overlayColor: Int = Color.argb(120, 183, 191, 207)

    private var presenter: VideoTrimmerContract.Presenter? = null
    private var adaptor: VideoFramesAdaptor? = null

    private var binding: LayoutVideoTrimmerBinding = LayoutVideoTrimmerBinding.inflate(LayoutInflater.from(context), this, true)

    /* -------------------------------------------------------------------------------------------*/
    /* Initialize */
    init {
//        inflate(context, R.layout.layout_video_trimmer, this)
        obtainAttributes(attrs)
        initViews()
    }

    private fun obtainAttributes(attrs: AttributeSet?) {
        attrs ?: return

        val array = resources.obtainAttributes(attrs, R.styleable.VideoTrimmerView)
        try {
            leftBarRes = array.getResourceId(R.styleable.VideoTrimmerView_vtv_window_left_bar, leftBarRes)
            binding.slidingWindowView.leftBarRes = leftBarRes

            rightBarRes = array.getResourceId(R.styleable.VideoTrimmerView_vtv_window_right_bar, rightBarRes)
            binding.slidingWindowView.rightBarRes = rightBarRes

            barWidth = array.getDimension(R.styleable.VideoTrimmerView_vtv_window_bar_width, barWidth)
            binding.slidingWindowView.barWidth = barWidth

            borderWidth = array.getDimension(R.styleable.VideoTrimmerView_vtv_window_border_width, borderWidth)
            binding.slidingWindowView.borderWidth = borderWidth

            borderColor = array.getColor(R.styleable.VideoTrimmerView_vtv_window_border_color, borderColor)
            binding.slidingWindowView.borderColor = borderColor

            overlayColor = array.getColor(R.styleable.VideoTrimmerView_vtv_overlay_color, overlayColor)
            binding.slidingWindowView.overlayColor = overlayColor
            Log.e("AMBO","Every thing got sortes here")
            Log.e("AMBO","Every thing got sortes here")
        } finally {
            array.recycle()
        }
    }

    private fun initViews() {
        binding.videoFrameListView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Attach / Detach */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter = obtainVideoTrimmerPresenter().apply { onViewAttached(this@VideoTrimmerView) }
        onPresenterCreated()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter?.onViewDetached()
        presenter = null
    }

    private fun onPresenterCreated() {
        presenter?.let {
            binding.slidingWindowView.listener = presenter as SlidingWindowView.Listener

            val horizontalMargin = (dpToPx(context, 11f) + barWidth).roundToInt()
            val decoration = VideoFramesDecoration(horizontalMargin, overlayColor)
            val scrollListener = VideoFramesScrollListener(
                horizontalMargin,
                presenter as VideoFramesScrollListener.Callback
            )

            binding.videoFrameListView.addItemDecoration(decoration)
            binding.videoFrameListView.addOnScrollListener(scrollListener)
        }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Public APIs */
    fun setVideo(video: File): VideoTrimmerView {
        presenter?.setVideo(video)
        return this
    }

    fun setMaxDuration(millis: Long): VideoTrimmerView {
        presenter?.setMaxDuration(millis)
        return this
    }

    fun setMinDuration(millis: Long): VideoTrimmerView {
        presenter?.setMinDuration(millis)
        return this
    }

    fun setFrameCountInWindow(count: Int): VideoTrimmerView {
        presenter?.setFrameCountInWindow(count)
        return this
    }

    fun setOnSelectedRangeChangedListener(listener: OnSelectedRangeChangedListener): VideoTrimmerView {
        presenter?.setOnSelectedRangeChangedListener(listener)
        return this
    }

    fun setExtraDragSpace(spaceInPx: Float): VideoTrimmerView {
        binding.slidingWindowView.extraDragSpace = spaceInPx
        return this
    }

    fun show() {
        presenter?.show()
    }

    fun getTrimmerDraft(): TrimmerDraft? = presenter?.getTrimmerDraft()

    fun restoreTrimmer(draft: TrimmerDraft) {
        presenter?.restoreTrimmer(draft)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* VideoTrimmerContract.View */
    override fun getSlidingWindowWidth(): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        val margin = dpToPx(context, 11f)
        return screenWidth - 2 * (margin + barWidth).roundToInt()
    }

    override fun setupAdaptor(video: File, frames: List<Long>, frameWidth: Int) {
        adaptor = VideoFramesAdaptor(video, frames, frameWidth).also {
            binding.videoFrameListView.adapter = it
            Log.e("AMBO",it.itemCount.toString())
            Log.e("AMBO","ok2")
        }
    }

    override fun setupSlidingWindow() {
        binding.slidingWindowView.reset()
    }

    override fun restoreSlidingWindow(left: Float, right: Float) {
        binding.slidingWindowView.setBarPositions(left, right)
    }

    override fun restoreVideoFrameList(framePosition: Int, frameOffset: Int) {
        val layoutManager = binding.videoFrameListView.layoutManager as? LinearLayoutManager ?: return
        layoutManager.scrollToPositionWithOffset(framePosition, frameOffset)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Listener */
    interface OnSelectedRangeChangedListener {
        fun onSelectRangeStart()
        fun onSelectRange(startMillis: Long, endMillis: Long)
        fun onSelectRangeEnd(startMillis: Long, endMillis: Long)
    }
}