package com.abhijith.videoaspectration

//import com.videotrimmer.library.utils.TrimVideo
import android.R.attr.*
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.Toast
import androidx.annotation.AnyRes
import androidx.appcompat.app.AppCompatActivity
import com.abhijith.videoaspectration.helper.FileUtil
import com.abhijith.videoaspectration.videotrimmerlib.VideoTrimmerView
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.FillModeCustomItem
import com.daasuu.mp4compose.composer.Mp4Composer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File


const val REQUEST_CODE_GALLERY_FILES = 10

class FilterActivity : AppCompatActivity(),
    VideoTrimmerView.OnSelectedRangeChangedListener, Player.EventListener {

    var startMillis: Long = 0
    var endMillis: Long = 0

    var actualHeight: Float = 0F
    var actualWidth: Float = 0F

    var actual_video_height = 0

    /*override fun onSeekProcessed() {
        super.onSeekProcessed()
        Log.e("ABHI",exoPlayer.player.currentPosition.toString())
        if(startMillis!=0L||endMillis!=0L){

        }
    }*/
    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "VideoTrimmer")
    }

    private val btnTrim by lazy {
        findViewById<Button>(R.id.btnTrim)
    }

    private fun playVideo(path: Uri, startMillis: Long, endMillis: Long) {
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(path)
            .let {
                ClippingMediaSource(
                    it,
                    startMillis * 1000L,
                    endMillis * 1000L
                )
            }

        exoPlayer.player.playWhenReady = true
        exoPlayer.prepare(source)
    }

    @SuppressLint("SdCardPath")
    fun runSafe(callback: (Uri) -> Unit) {
        if (this::uri.isInitialized) {
            callback(uri)
        }
    }

    val exoPlayer by lazy {
        findViewById<MySimpleExoPlayer>(R.id.mySimpleExoPlayer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)

        findViewById<Button>(R.id.one_one).setOnClickListener {
            runSafe {
                val fillModeCustomItem = FillModeCustomItem(
                    1f,
                    0f,
                    0.toFloat(),
                    0.toFloat(),
                    (RATIO.OneOne.width).toFloat(),  // the video Width = W pixel
                    (RATIO.OneOne.height).toFloat()// the video Height = H pixel
                )
                compress(
                    fillModeCustomItem,
                    FileUtil.from(this@FilterActivity, uri),
                    exoPlayer,
                    RATIO.OneOne
                )
            }
        }

        btnTrim.setOnClickListener {
            trim(FileUtil.from(this, uri))
        }


        findViewById<Button>(R.id.btnpickfile).setOnClickListener {

            Dexter.withContext(this@FilterActivity)
                .withPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report.let {
                            val intent = Intent()
                            intent.type = "video/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            startActivityForResult(
                                Intent.createChooser(intent, "Select Video"),
                                REQUEST_CODE_GALLERY_FILES
                            )
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                })
                .withErrorListener {
                    Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
                }
                .check()

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
                compress(fillModeCustomItem, FileUtil.from(this, uri), exoPlayer, RATIO.SixTeenNine)
            }
        }

        findViewById<Button>(R.id.three_two).setOnClickListener {
            runSafe {

                /*val resizeOption = VideoResizeOption.Builder()
                    .setVideoResolutionType(VideoResolutionType.AS720)
                    .setVideoBitrate(720 * 720)
                    .setAudioBitrate(128 * 1000)
                    .setAudioChannel(1)
                    .setScanRequest(ScanRequest.TRUE)
                    .build()
                val file = File("/storage/emulated/0/Filtered Videos", "mono.mp4")
                val option = ResizeOption.Builder()
                    .setMediaType(MediaType.VIDEO)
                    .setVideoResizeOption(resizeOption)
                    .setTargetPath(FileUtil.from(this, uri).absolutePath)
                    .setOutputPath(file.absolutePath)
                    .setCallback { code, output ->
                        toast("complete",Toast.LENGTH_SHORT)
                    }
                    .build()*/

                /*MediaResizer.process(option)*/
                var fillModeCustomItem = FillModeCustomItem(
                    1f,
                    0f,
                    0.toFloat(),
                    0.toFloat(),
                    (RATIO.ThreeTwo.width).toFloat(),  // the video Width = W pixel
                    (RATIO.ThreeTwo.height).toFloat() // the video Height = H pixel
                )
                compress(fillModeCustomItem, FileUtil.from(this, uri), exoPlayer, RATIO.ThreeTwo)
            }
        }

    }

    private fun compress(
        fillModeCustomItem: FillModeCustomItem,
        f: File,
        exoPlayer: MySimpleExoPlayer,
        ratio: RATIO
    ) {
        if (!File("/storage/emulated/0/Filtered Videos").exists())
            File("/storage/emulated/0/Filtered Videos").mkdir()
        val file = File("/storage/emulated/0/Filtered Videos", "${System.currentTimeMillis()}.mp4")
        Toast.makeText(this@FilterActivity, "Start", Toast.LENGTH_SHORT).show()
        Mp4Composer(
            Uri.fromFile(f),
            file.absolutePath,
            this@FilterActivity
        ).apply {
            //                rotation(Rotation.ROTATION_90)
            size(ratio.width, ratio.height)
            fillMode(FillMode.CUSTOM)
                .customFillMode(fillModeCustomItem)
                //.trim(startMillis, endMillis)
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
                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(file?.extension)
                            MediaScannerConnection.scanFile(
                                this@FilterActivity,
                                arrayOf(file.absolutePath),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                Log.d(
                                    "TAG",
                                    "Image capture scanned into media store: $uri"
                                )
                            }

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

    private fun trim(f: File) {
        if (!File("/storage/emulated/0/Filtered Videos").exists())
            File("/storage/emulated/0/Filtered Videos").mkdir()
        val file =
            File("/storage/emulated/0/Filtered Videos", "trim ${System.currentTimeMillis()}.mp4")
        Toast.makeText(this@FilterActivity, "Start", Toast.LENGTH_SHORT).show()
        Mp4Composer(
            Uri.fromFile(f),
            file.absolutePath,
            this@FilterActivity
        ).apply {
            //                rotation(Rotation.ROTATION_90)
            trim(startMillis, endMillis)
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
                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(file.extension)
                            MediaScannerConnection.scanFile(
                                this@FilterActivity,
                                arrayOf(file.absolutePath),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                Log.d(
                                    "TAG",
                                    "Image capture scanned into media store: $uri"
                                )
                            }

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

    lateinit var uri: Uri

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY_FILES) {
                uri = data!!.data!!
                exoPlayer.play(uri)
                runOnUiThread {
                    val metaRetriever = MediaMetadataRetriever()
                    metaRetriever.setDataSource(FileUtil.from(this, uri).absolutePath)
                    actualHeight =
                        metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                            .toFloat()
                    actualWidth =
                        metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!
                            .toFloat()

                    exoPlayer.player.addListener(this)
                    findViewById<VideoTrimmerView>(R.id.videoTrimmerView).apply {
                        setVideo(FileUtil.from(this@FilterActivity, uri))
                            .setMaxDuration(60_000)                   // millis
                            .setMinDuration(1000)                    // millis
                            .setFrameCountInWindow(10)
//                            .setExtraDragSpace(dpToPx(10f))                    // pixels
                            .setOnSelectedRangeChangedListener(this@FilterActivity)
                            .show()
                    }
                }

                Log.e("ABHIIIII", "" + FileUtil.from(this, uri).absolutePath)
            }
        }
    }

    override fun onSelectRange(startMillis: Long, endMillis: Long) {

    }


    override fun onSelectRangeEnd(startMillis: Long, endMillis: Long) {
        exoPlayer.player.seekTo(startMillis)
        playVideo(uri, startMillis, endMillis)
        this.startMillis = startMillis
        this.endMillis = endMillis
//        Log.e("BRO","$startMillis, $endMillis")
//        exoPlayer.player.addListener(object :Player.EventListener{
//            override fun onSeekProcessed() {
//                super.onSeekProcessed()
//                if(exoPlayer.player.currentPosition>=endMillis){
//                    exoPlayer.player.seekTo(startMillis)
//                }
//            }
//        })
    }

    override fun onSelectRangeStart() {

    }

}

internal fun Context.getResourceUri(@AnyRes resourceId: Int): Uri =
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_FILE)
        .authority(packageName)
        .path(resourceId.toString())
        .build()

sealed class RATIO(val width: Int, val height: Int) {
    object OneOne : RATIO(720, 720)
    object SixTeenNine : RATIO(720, 406)
    object ThreeTwo : RATIO(720, 900)
}


//1:1

private fun Context.dpToPx(dp: Float): Float {
    val density = resources.displayMetrics.density
    return dp * density
}