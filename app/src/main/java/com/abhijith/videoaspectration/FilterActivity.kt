package com.abhijith.videoaspectration

import android.R.attr.*
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.appcompat.app.AppCompatActivity
import com.abhijith.videoaspectration.helper.FileUtil
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.FillModeCustomItem
import com.daasuu.mp4compose.composer.Mp4Composer
import java.io.File


const val REQUEST_CODE_GALLERY_FILES = 10

class FilterActivity: AppCompatActivity() {
    @SuppressLint("SdCardPath")
    fun runSafe(callback: (Uri) -> Unit){
        if(this::uri.isInitialized){
            callback(uri)
        }
    }
    val exoPlayer by lazy {
        findViewById<MySimpleExoPlayer>(R.id.mySimpleExoPlayer)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.filter_activity)

//        val f = File(intent.getStringExtra(video_path)!!)

//        Toast.makeText(this, f.absolutePath, Toast.LENGTH_SHORT).show()


//        exoPlayer.apply {
//            play(uri)
//        }

        findViewById<Button>(R.id.one_one).setOnClickListener {
            runSafe{
                var fillModeCustomItem = FillModeCustomItem(
                    1f,
                    0f,
                    0.toFloat(),
                    0.toFloat(),
                    (RATIO.OneOne.width).toFloat(),  // the video Width = W pixel
                    (RATIO.OneOne.height).toFloat()// the video Height = H pixel
                )
                compress(fillModeCustomItem,FileUtil.from(this, uri), exoPlayer, RATIO.OneOne)
            }
        }



        findViewById<Button>(R.id.btnpickfile).setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Video"),
                REQUEST_CODE_GALLERY_FILES
            )

        }

        findViewById<Button>(R.id.sixteen_nine).setOnClickListener {
            runSafe {
                var fillModeCustomItem = FillModeCustomItem(
                    1f,
                    0f,
                    0.toFloat(),
                    0.toFloat(),
                    (RATIO.SixTeenNine.width).toFloat(),  // the video Width = W pixel
                    (RATIO.SixTeenNine.height).toFloat() //// the video Height = H pixel
                )
                compress(fillModeCustomItem,FileUtil.from(this, uri), exoPlayer, RATIO.SixTeenNine)
            }
        }

        findViewById<Button>(R.id.three_two).setOnClickListener {
            runSafe {

                var fillModeCustomItem = FillModeCustomItem(
                    1f,
                    0f,
                    0.toFloat(),
                    0.toFloat(),
                    (RATIO.ThreeTwo.width).toFloat(),  // the video Width = W pixel
                    (RATIO.ThreeTwo.height).toFloat() // the video Height = H pixel
                )
                compress(fillModeCustomItem,FileUtil.from(this, uri), exoPlayer, RATIO.ThreeTwo)
            }
        }

    }

    private fun compress(fillModeCustomItem: FillModeCustomItem,f: File, exoPlayer: MySimpleExoPlayer, ratio: RATIO) {
        val file = File("/storage/emulated/0/Filtered Videos", "mono.mp4")
        Toast.makeText(this@FilterActivity, "Start", Toast.LENGTH_SHORT).show()
        Mp4Composer(
            f.absolutePath,
            file.absolutePath
        ).apply {
    //                rotation(Rotation.ROTATION_90)
            size(ratio.width, ratio.height)
                .fillMode(FillMode.CUSTOM)
                .customFillMode(fillModeCustomItem)
//                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .listener(object : Mp4Composer.Listener {
                    override fun onProgress(progress: Double) {
                        Log.e("ABHIIIII", progress.toString())
                    }

                    override fun onCurrentWrittenVideoTime(timeUs: Long) {

                    }

                    override fun onCompleted() {
                        runOnUiThread {
                            Toast.makeText(this@FilterActivity, "End", Toast.LENGTH_SHORT).show()
                            exoPlayer.freeMemory()
                            exoPlayer.play(Uri.fromFile(file))
                        }
                    }

                    override fun onCanceled() {

                    }

                    override fun onFailed(exception: Exception?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@FilterActivity,
                                "Error ${exception.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("ABHIIIII", "${exception.toString()}")
                        }
                    }
                })
            start()
        }
    }

    lateinit var uri:Uri

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_GALLERY_FILES){
                uri = data!!.data!!
                exoPlayer.play(uri)
                Log.e("ABHIIIII", "" + FileUtil.from(this, uri).absolutePath)
            }
        }
    }

}
internal fun Context.getResourceUri(@AnyRes resourceId: Int): Uri =
    Uri.Builder()
    .scheme(ContentResolver.SCHEME_FILE)
    .authority(packageName)
    .path(resourceId.toString())
    .build()

sealed class RATIO(val width: Int, val height: Int){
    object OneOne:RATIO(1080, 1080)
    object SixTeenNine:RATIO(1920, 1080)
    object ThreeTwo:RATIO(720, 480)
}



//1:1

