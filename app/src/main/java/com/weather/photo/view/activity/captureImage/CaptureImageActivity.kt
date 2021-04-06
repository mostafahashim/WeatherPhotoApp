package com.weather.photo.view.activity.captureImage

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.weather.photo.R
import com.weather.photo.cameraCore.Camera2
import com.weather.photo.cameraCore.CameraPreview
import com.weather.photo.databinding.ActivityCaptureImageBinding
import com.weather.photo.locationUtil.turnGPSOn
import com.weather.photo.observer.OnAskUserAction
import com.weather.photo.observer.OnEditPlaceData
import com.weather.photo.util.*
import com.weather.photo.view.activity.baseActivity.BaseActivity
import com.weather.photo.view.sub.PopupDialogEditPlace
import java.io.File

class CaptureImageActivity : BaseActivity(
    R.string.app_name, false, false, false,
    false, false, false, false,
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
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
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
        updateLocationUI()
    }

    override fun setListener() {
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

            binding.layoutCapture.visibility = View.INVISIBLE
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

    override fun onShowHideLoadingProgress(isShow: Boolean) {
        showHideProgressDialog(isShow)
    }

    override fun onShowHideMessageDialog(title: String, message: String, isShow: Boolean) {
        showHideMessageDialog(isShow, getString(R.string.error), message)
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.BLACK)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    override fun onFinishWithSuccess() {
        if (binding.viewModel?.isShowLoader?.value!!) {
            //prevent save image without weather data
            Toast.makeText(
                this@CaptureImageActivity,
                getString(R.string.please_wait_until_fetching_location_data),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        onShowHideLoadingProgress(true)
        Handler(Looper.getMainLooper()).postDelayed({
            var bitmap = getBitmapFromView(binding.finalPhoto)
            val imagePath = DataProvider().saveImage(
                bitmap!!,
                this@CaptureImageActivity
            )
            if (binding.viewModel?.isSavedFileInitialized()!! && binding.viewModel?.savedFile != null && binding.viewModel?.savedFile!!.exists()) {
                binding.viewModel?.savedFile?.delete()
                //then delete it from media store (gallery)
                DataProvider().deleteFileFromMediaStore(
                    contentResolver,
                    binding.viewModel?.savedFile!!
                )
            }
            binding.viewModel?.savedFile = File(imagePath)
            onShowHideLoadingProgress(false)
            var intent = Intent()
            intent.putExtra("ImageFile", binding.viewModel?.savedFile)
            setResult(RESULT_OK, intent)
            finish_activity()
        }, 500)
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

    override fun openLocationDetails() {
        var popupDialogEditPlace = PopupDialogEditPlace()
        popupDialogEditPlace.setOnEditPlaceDataObserver(object : OnEditPlaceData {
            override fun onEditPlaceName(name: String) {
                binding.viewModel?.placeName?.value = name
            }

        })
        var bundle = Bundle()
        bundle.putString("placeName", binding.viewModel?.placeName?.value)
        popupDialogEditPlace.arguments = bundle
        popupDialogEditPlace.isCancelable = true
        popupDialogEditPlace.show(supportFragmentManager, "PopupDialogEditPlace")
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

    override fun onDestroy() {
        super.onDestroy()
        if (::locationManager.isInitialized)
            locationManager.removeUpdates(locationListener)
    }

    // location retrieved by the Fused Location Provider.
    lateinit var locationManager: LocationManager

    private fun updateLocationUI() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                GalleryConstants.REQUEST_Permission_Gallery
            )
            return
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10.toLong(),
                0.1.toFloat(), locationListener
            )
        } else {
            askUserTurnGPS()
        }
    }

    var locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (binding.viewModel?.mLastKnownLocation == null) {
                binding.viewModel?.mLastKnownLocation = location
                binding.viewModel!!.getWeatherDataApi()
            }
        }

        override fun onProviderDisabled(provider: String) {
            askUserTurnGPS()
        }

        override fun onProviderEnabled(provider: String) {
            updateLocationUI()
        }
    }

    fun askUserTurnGPS() {
        turnGPSOn(this@CaptureImageActivity, object : OnAskUserAction {
            override fun onPositiveAction() {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, LocationPermissionRequest)
            }

            override fun onNegativeAction() {
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GalleryConstants.REQUEST_Permission_Gallery -> {
                // If request is cancelled, the result arrays are empty.
                initializeViews()
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationPermissionRequest) {
            updateLocationUI()
        }
    }

}