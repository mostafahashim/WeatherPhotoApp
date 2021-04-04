package com.weather.photo.view.activity.captureImage

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.weather.photo.R
import com.weather.photo.cameraCore.Camera2
import com.weather.photo.cameraCore.CameraPreview
import com.weather.photo.databinding.ActivityCaptureImageBinding
import com.weather.photo.util.DataProvider
import com.weather.photo.util.GalleryConstants
import com.weather.photo.view.activity.baseActivity.BaseActivity
import java.io.File

class CaptureImageActivity : BaseActivity(
    R.string.app_name, false, false, false,
    false, false, false, false, false,
), CaptureImageViewModel.Observer {

    lateinit var binding: ActivityCaptureImageBinding
    override fun doOnCreate(arg0: Bundle?) {
        binding = putContentView(R.layout.activity_capture_image) as ActivityCaptureImageBinding
        binding.viewModel =
            ViewModelProvider(
                this,
                CaptureImageViewModelFactory(application)
            )
                .get(CaptureImageViewModel::class.java)
        binding.viewModel!!.baseViewModelObserver = baseViewModelObserver
        binding.viewModel!!.observer = this
        binding.lifecycleOwner = this
        initializeViews()
        setListener()
    }


    override fun initializeViews() {
        onPrepareNewImageViews()

        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ),
                GalleryConstants.REQUEST_Permission_Gallery
            )
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            binding.viewModel?.mCameraPreview = CameraPreview(this@CaptureImageActivity)
            binding.cameraPreviewPreLoliPop.addView(binding.viewModel?.mCameraPreview)
        } else {
            binding.viewModel?.camera2 = Camera2(this, binding.cameraView)
        }
        onPrepareNewImageViews()
    }

    override fun onPrepareNewImageViews() {
        runOnUiThread {
            if (binding.viewModel?.isSavedFileInitialized()!! && binding.viewModel?.savedFile != null
                && binding.viewModel?.savedFile!!.exists()
            ) {
                binding.viewModel?.savedFile?.delete()
                //then delete it from media store (gallery)
                DataProvider().deleteFileFromMediaStore(
                    contentResolver,
                    binding.viewModel?.savedFile!!
                )
            }
            binding.cameraPreviewPreLoliPop.visibility = View.GONE
            binding.cameraView.visibility = View.GONE
            binding.ivImageCaptured.visibility = View.GONE
            binding.layoutDoneRetry.visibility = View.GONE

            binding.layoutFlash.visibility = View.VISIBLE
            binding.layoutCapture.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                binding.cameraPreviewPreLoliPop.visibility = View.VISIBLE
                binding.layoutFlash.visibility = View.GONE
                binding.ivSwitchCamera.visibility = View.GONE
                if (binding.viewModel?.ismCameraPreviewInitialized()!!)
                    binding.viewModel?.mCameraPreview?.resumeCamera()
            } else {
                binding.cameraView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.layoutFlash.visibility =
                        if (binding.viewModel?.isCamera2Initialized()!! && binding.viewModel?.camera2?.checkSupportFlash()!!) View.VISIBLE else View.GONE
                }, 500)
            }
        }
    }

    private fun prepareCapturedImageViews() {
        runOnUiThread {
            binding.cameraView.visibility = View.GONE
            binding.cameraPreviewPreLoliPop.visibility = View.GONE

            binding.layoutCapture.visibility = View.GONE
            binding.cameraView.visibility = View.GONE
            binding.layoutFlash.visibility = View.GONE

            binding.ivImageCaptured.visibility = View.VISIBLE
            binding.layoutDoneRetry.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        finishWithCancel()
        super.onBackPressed()
    }

    override fun finishWithCancel() {
        if (binding.viewModel?.isSavedFileInitialized()!! && binding.viewModel?.savedFile != null && binding.viewModel?.savedFile!!.exists()) {
            binding.viewModel?.savedFile?.delete()
            //then delete it from media store (gallery)
            DataProvider().deleteFileFromMediaStore(
                contentResolver,
                binding.viewModel?.savedFile!!
            )
        }
        setResult(RESULT_CANCELED, Intent())
        finish_activity()
    }

    override fun onSwitchCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (binding.viewModel?.isCamera2Initialized()!!) {
                binding.viewModel?.camera2?.switchCamera()
                binding.layoutFlash.visibility =
                    if (binding.viewModel?.camera2!!.checkSupportFlash()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onStartCapture() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (binding.viewModel?.ismCameraPreviewInitialized()!!)
                binding.viewModel?.mCameraPreview?.takePhoto { bitmap ->
                    //save image to check orientation
                    runOnUiThread {
                        Handler(Looper.getMainLooper()).postDelayed({
                            prepareCapturedImageViews()
                            val imagePath = DataProvider().saveImage(
                                bitmap,
                                this@CaptureImageActivity
                            )
                            binding.viewModel?.capturedBitmap =
                                DataProvider().getOriginalRotationForBitmap(imagePath, bitmap)!!
                            binding.ivImageCaptured.setImageBitmap(binding.viewModel?.capturedBitmap)
                            // delete the prev file
                            var tempCaptueredFile = File(imagePath)
                            if (tempCaptueredFile != null && tempCaptueredFile.exists())
                                tempCaptueredFile.delete()
                            //then delete it from media store (gallery)
                            DataProvider().deleteFileFromMediaStore(
                                contentResolver,
                                tempCaptueredFile
                            )
                            val newImagePath = DataProvider().saveImage(
                                binding.viewModel?.capturedBitmap!!,
                                this@CaptureImageActivity
                            )
                            binding.viewModel?.savedFile = File(newImagePath)
                        }, 200)
                    }
                }
        } else {
            if (binding.viewModel?.isCamera2Initialized()!!)
                binding.viewModel?.camera2?.takePhoto { bitmap ->
                    runOnUiThread {
                        prepareCapturedImageViews()
                        binding.viewModel?.capturedBitmap = bitmap
                        binding.ivImageCaptured.setImageBitmap(binding.viewModel?.capturedBitmap)
                        val imagePath = DataProvider().saveImage(
                            bitmap,
                            this@CaptureImageActivity
                        )
                        binding.viewModel?.savedFile = File(imagePath)
                    }
                }
        }
    }

    override fun onFinishWithSuccess() {
        var intent = Intent()
        intent.putExtra("ImageFile", binding.viewModel?.savedFile)
        setResult(RESULT_OK, intent)
        finish_activity()
    }

    override fun setCameraFlashOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (binding.viewModel?.isCamera2Initialized()!!)
                binding.viewModel?.camera2?.setFlash(Camera2.FLASH.ON)
            binding.ivCameraFlashOn.alpha = 1f
            binding.ivCameraFlashAuto.alpha = 0.4f
            binding.ivCameraFlashOff.alpha = 0.4f
        }
    }

    override fun setCameraFlashOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (binding.viewModel?.isCamera2Initialized()!!)
                binding.viewModel?.camera2?.setFlash(Camera2.FLASH.OFF)
            binding.ivCameraFlashOff.alpha = 1f
            binding.ivCameraFlashOn.alpha = 0.4f
            binding.ivCameraFlashAuto.alpha = 0.4f
        }
    }

    override fun setCameraFlashAuto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.ivCameraFlashOff.alpha = 0.4f
            binding.ivCameraFlashOn.alpha = 0.4f
            binding.ivCameraFlashAuto.alpha = 1f
            if (binding.viewModel?.isCamera2Initialized()!!)
                binding.viewModel?.camera2?.setFlash(Camera2.FLASH.AUTO)
        }
    }

    override fun setListener() {
    }

    override fun onPause() {
        //  cameraPreview.pauseCamera()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        } else {
            if (binding.viewModel?.isCamera2Initialized()!!)
                binding.viewModel?.camera2?.close()
        }
        super.onPause()
    }

    override fun onResume() {
        // cameraPreview.resumeCamera()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (binding.viewModel?.ismCameraPreviewInitialized()!!)
                binding.viewModel?.mCameraPreview?.resumeCamera()
        } else {
            if (binding.viewModel?.isCamera2Initialized()!!)
                binding.viewModel?.camera2?.onResume()
        }
        onPrepareNewImageViews()
        super.onResume()
    }

}