package com.example.flashlight

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.example.flashlight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var cameraManager : CameraManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        setupAppName()

        binding.apply {
            val hasFlashLight = applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

            if (!hasFlashLight){
                light.isEnabled = false
                Toast.makeText(this@MainActivity, "This device does not have a FlashLight", Toast.LENGTH_LONG).show()
            }

            layoutMain.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {}

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                    try {
                        val cameraIdList = cameraManager.cameraIdList
                        var cameraId: String? = null

                        for (id in cameraIdList) {
                            val characteristics = cameraManager.getCameraCharacteristics(id)
                            val flashAvailable =
                                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                            if (flashAvailable == true) {
                                cameraId = id
                                break
                            }
                        }

                        if (cameraId != null) {
                            if (currentId == motionLayout!!.endState) {
                                cameraManager.setTorchMode(cameraId, true)
                            } else {
                                cameraManager.setTorchMode(cameraId, false)
                            }
                        } else {
                             Toast.makeText(
                                 this@MainActivity,
                                 "No camera with flash available on your device.",
                                 Toast.LENGTH_SHORT
                             ).show()
                        }
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {}
            })
        }

    }
    private fun setupAppName() {
        val spannableString = SpannableString(getString(R.string.flashlight))
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this,R.color.clr_green))
        spannableString.setSpan(colorSpan,5,10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.title.text = spannableString
    }
}