package com.abhijith.videoaspectration

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView

class FilterPreview(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {

    init {
        inflate(context,R.layout.filterpreview,this)
    }

    fun setPreviewItems(vararg imageView: ImageView){
        findViewById<LinearLayout>(R.id.itemContainer).apply {
            imageView.forEach {
                this.addView(it)

            }
        }
    }

    interface FilterChanged{
        fun onFilterChanged()
    }
}