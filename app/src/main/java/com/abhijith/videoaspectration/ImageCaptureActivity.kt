package com.abhijith.videoaspectration

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.abhijith.videoaspectration.databinding.ActivityImageCaptureBinding
import com.abhijith.videoaspectration.helper.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import java.io.File

class ImageCaptureActivity : AppCompatActivity() {

    private lateinit var camera: Camera
    private lateinit var imageCapture: ImageCapture
    private val capturedImageFile:File by lazy{
        val f = File(Environment.getExternalStorageDirectory(), folder_main)
        if (!f.exists()) {
            f.mkdirs()
        }
        val fileName = String.format("%d.jpeg", System.currentTimeMillis())
        File(f.absolutePath, fileName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityImageCaptureBinding.inflate(layoutInflater).apply {
            setContentView(root)
            requestRuntimePermission()
            btnCaptureImage.setOnClickListener {
                onCaptureImage()
            }
        }
    }

    private fun ActivityImageCaptureBinding.requestRuntimePermission() {
        val multiplePermissionsListener = object : ShortenMultiplePermissionListener() {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    setUpCamera()
                } else {
                    onPermissionDenied()
                }
            }
        }

        Dexter.withContext(this@ImageCaptureActivity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            .withListener(multiplePermissionsListener)
            .check()

    }

    private fun ActivityImageCaptureBinding.setupCameraProvider() {
        ProcessCameraProvider
            .getInstance(this@ImageCaptureActivity)
            .also { provider ->
                provider.addListener({
                    bindPreview(provider.get())
                }, ContextCompat.getMainExecutor(this@ImageCaptureActivity))
            }
    }

    private fun ActivityImageCaptureBinding.bindPreview(cameraProvider: ProcessCameraProvider) {

        val preview: Preview = Preview
            .Builder()
//            .setTargetResolution(four_to_five)
            .build()

        imageCapture = ImageCapture
            .Builder()
//            .setTargetResolution(Size(four_to_five.width*2,four_to_five.height*2))
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
//        previewView.layoutParams.height = four_to_five.height
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(
            this@ImageCaptureActivity,
            cameraSelector,
            preview,
            imageCapture
        )

        camera.let {
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    }

    private fun onCaptureImage() {

        val imageSavedCallback = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                makeToast(getString(R.string.image_capture_success))
                startActivity(Intent(this@ImageCaptureActivity,ImageCroppingActivity::class.java).apply {
                    putExtra(ImageCroppingActivity.ImagePath,capturedImageFile.absolutePath)
                    makeToast(capturedImageFile.absolutePath)
                })

            }

            override fun onError(exception: ImageCaptureException) {
                makeToast(
                    getString(
                        R.string.image_capture_error,
                        exception.message,
                        exception.imageCaptureError
                    )
                )
            }
        }
        val outputFileOptions: ImageCapture.OutputFileOptions =
            ImageCapture.OutputFileOptions.Builder(capturedImageFile).build()
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            imageSavedCallback
        )
    }

    private fun ActivityImageCaptureBinding.setUpCamera() {
        setupCameraProvider()
    }

    private fun ActivityImageCaptureBinding.onPermissionDenied() {
        makeToast(getString(R.string.permission_denied))
        finish()
    }
}

fun map(
    value: Int,
    rangeFromStart: Int,
    RangeFromEnd: Int,
    rangeToStart: Int,
    rangeTwoEnd: Int
): Int {
    return rangeToStart + ((rangeTwoEnd - rangeToStart) / (RangeFromEnd - rangeFromStart)) * (value - rangeFromStart)
}
