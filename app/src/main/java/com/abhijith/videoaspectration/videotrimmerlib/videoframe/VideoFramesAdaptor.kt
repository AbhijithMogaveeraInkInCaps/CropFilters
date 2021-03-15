package com.abhijith.videoaspectration.videotrimmerlib.videoframe

import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.abhijith.videoaspectration.R
import com.abhijith.videoaspectration.videotrimmerlib.tools.SetVideoThumbnailAsyncTask
import java.io.File

internal class VideoFramesAdaptor(
        private val video: File,
        private val frames: List<Long>,
        private val frameWidth: Int
) : RecyclerView.Adapter<VideoFramesAdaptor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(frameWidth, MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView as ImageView
        val frame = frames[position]
//        view.setImageResource(R.drawable.pic)
        SetVideoThumbnailAsyncTask(view, frame).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, video)
    }

    override fun getItemCount(): Int = frames.size-3

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}