package com.abhijith.videoaspectration.aother;

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abhijith.videoaspectration.MainActivity
import com.abhijith.videoaspectration.R
import com.lb.video_trimmer_library.interfaces.VideoTrimmingListener
import java.io.File


class TrimmerActivity2 : AppCompatActivity(), VideoTrimmingListener {
//    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trimmer_layout)
        val inputVideoUri: Uri? = intent?.getParcelableExtra(EXTRA_INPUT_URI)
        if (inputVideoUri == null) {
            finish()
            return
        }
        //setting progressbar
//        progressDialog = ProgressDialog(this)
//        progressDialog!!.setCancelable(false)
//        progressDialog!!.setMessage(getString(R.string.trimming_progress))
        findViewById<VideoTrimmerView>(R.id.videoTrimmerView).setMaxDurationInMs(10 * 1000)
        findViewById<VideoTrimmerView>(R.id.videoTrimmerView).setOnK4LVideoListener(this)
        val parentFolder = getExternalFilesDir(null)!!
        parentFolder.mkdirs()
        val fileName = "trimmedVideo_${System.currentTimeMillis()}.mp4"
        val trimmedVideoFile = File(parentFolder, fileName)
        findViewById<VideoTrimmerView>(R.id.videoTrimmerView).setDestinationFile(trimmedVideoFile)
        findViewById<VideoTrimmerView>(R.id.videoTrimmerView).setVideoURI(inputVideoUri)
        findViewById<VideoTrimmerView>(R.id.videoTrimmerView).setVideoInformationVisibility(true)
    }

    override fun onTrimStarted() {
        findViewById<LinearLayout>(R.id.trimmingProgressView).visibility = View.VISIBLE
    }

    override fun onFinishedTrimming(uri: Uri?) {
        findViewById<LinearLayout>(R.id.trimmingProgressView).visibility = View.GONE
        if (uri == null) {
            Toast.makeText(this@TrimmerActivity2, "failed trimming", Toast.LENGTH_SHORT).show()
        } else {
            val msg = getString(R.string.video_saved_at, uri.path)
            Toast.makeText(this@TrimmerActivity2, msg, Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setDataAndType(uri, "video/mp4")
            startActivity(intent)
        }
        finish()
    }

    override fun onErrorWhileViewingVideo(what: Int, extra: Int) {
        findViewById<LinearLayout>(R.id.trimmingProgressView).visibility = View.GONE
        Toast.makeText(this@TrimmerActivity2, "error while previewing video", Toast.LENGTH_SHORT).show()
    }

    override fun onVideoPrepared() {
        //        Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
    }

    override fun onSelection(startMilliSecond: Long, endMilliSecond: Long) {
        super.onSelection(startMilliSecond, endMilliSecond)

    }
}