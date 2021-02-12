package com.abhijith.videoaspectration

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abhijith.videoaspectration.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            image.setOnClickListener {
                Intent(this@MainActivity,ImageCaptureActivity::class.java).apply {
                    startActivity(this)
                }
            }
            video.setOnClickListener {
                Intent(this@MainActivity,VideoCaptureActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }


}