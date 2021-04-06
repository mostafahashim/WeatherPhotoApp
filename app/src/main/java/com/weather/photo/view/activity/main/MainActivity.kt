package com.weather.photo.view.activity.main

import android.content.ContentUris
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.stfalcon.imageviewer.StfalconImageViewer
import com.weather.photo.R
import com.weather.photo.databinding.ActivityMainBinding
import com.weather.photo.model.GalleryModel
import com.weather.photo.posterUtil.PosterOverlayView
import com.weather.photo.util.DataProvider
import com.weather.photo.util.RequestCodeCaptureActivity
import com.weather.photo.util.ScreenSizeUtils
import com.weather.photo.view.activity.baseActivity.BaseActivity
import com.weather.photo.view.activity.captureImage.CaptureImageActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : BaseActivity(
    R.string.app_name, true, false, true,
    false, false, false, true,
), MainViewModel.Observer {

    lateinit var binding: ActivityMainBinding
    override fun doOnCreate(arg0: Bundle?) {
        binding = putContentView(R.layout.activity_main) as ActivityMainBinding
        binding.viewModel =
            ViewModelProvider(
                this,
                MainViewModelFactory(application)
            )
                .get(MainViewModel::class.java)
        binding.viewModel!!.baseViewModelObserver = baseViewModelObserver
        binding.viewModel!!.observer = this
        binding.lifecycleOwner = this
        initializeViews()
        setListener()
    }


    override fun initializeViews() {
        var isLandScape = false
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true
            binding.recyclerView.layoutManager = GridLayoutManager(this, 4)
//            var screenWidth: Int = ScreenSizeUtils().getScreenWidth(this)
//            binding.viewModel!!.updateBooksAdapterColumnWidth(screenWidth)
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isLandScape = false
            binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
//            var screenWidth: Int = ScreenSizeUtils().getScreenWidth(this)
//            binding.viewModel!!.updateBooksAdapterColumnWidth(screenWidth)
        }

        var screenWidth: Int = ScreenSizeUtils().getScreenWidth(this)
        binding.viewModel!!.updateBooksAdapterColumnWidth(screenWidth, isLandScape)
        setHomeButtonVisibility(true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.recyclerView.layoutManager = GridLayoutManager(this, 4)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        }
    }

    private fun setHomeButtonVisibility(isVisible: Boolean) {
        binding.ivHomeButton.visibility =
            if (isVisible) View.VISIBLE else View.GONE
        if (isVisible) {
            val anim = ScaleAnimation(
                0.8f, 1f,  // Start and end values for the X axis scaling
                0.8f, 1f,  // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f
            ) // Pivot point of Y scaling
            // Needed to keep the result of the animation
            anim.fillAfter = true
            anim.duration = 100
            anim.repeatCount = 8
            anim.repeatMode = Animation.REVERSE
            binding.ivHomeButton.startAnimation(anim)
        }
    }

    override fun setListener() {
        binding.viewModel!!.isShowNoData.removeObservers(this)
        binding.viewModel!!.isShowNoData.observe(this, Observer {
            if (it && lifecycle.currentState == Lifecycle.State.RESUMED) {
                binding.layoutNoData.tvBack.visibility = View.INVISIBLE
            }
        })
    }

    private var doubleBackToExitPressedOnce: Boolean = false

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            if (doubleBackToExitPressedOnce) {
                finish_activity()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(
                this, getString(R.string.press_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else {
            super.onBackPressed()
        }
    }

    override fun onButtonAddClicked() {
        var intent = Intent(this@MainActivity, CaptureImageActivity::class.java)
        startActivityForResult(intent, RequestCodeCaptureActivity)
        overridePendingTransition(R.anim.slide_from_right_to_left, R.anim.slide_in_left)
    }

    private var viewer: StfalconImageViewer<GalleryModel>? = null
    private var overlayView: PosterOverlayView? = null

    private fun setupOverlayView(startPosition: Int) {
        overlayView = PosterOverlayView(this).apply {
            update(binding.viewModel!!.galleryModels!![startPosition])

            onShareClick = { galleryModel ->
                try {
                    val image = File(galleryModel.sdcardPath)
                    var bitmap =
                        BitmapFactory.decodeFile(image.absolutePath, BitmapFactory.Options())
                    if (bitmap != null) {
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "image/*"
                        val bytes = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                        val path: String = MediaStore.Images.Media.insertImage(
                            contentResolver,
                            bitmap, getString(R.string.app_name), null
                        )
                        val imageUri = Uri.parse(path)
                        share.putExtra(Intent.EXTRA_STREAM, imageUri)
                        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name))
                        startActivity(Intent.createChooser(share, getString(R.string.share_via)))
                    } else {
                        Toast.makeText(
                            this@MainActivity, context.getString(R.string.cant_find_the_image_file),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun openViewer(startPosition: Int, imageView: ImageView) {
        setupOverlayView(startPosition)
        viewer = StfalconImageViewer.Builder(
            this, binding.viewModel!!.galleryModels
        ) { iv: ImageView, galleryModel: GalleryModel ->
            Glide.with(this).load("file://" + galleryModel.sdcardPath)
                .into(iv)
        }
            .withStartPosition(startPosition)
            .withTransitionFrom(imageView)
            .withImageChangeListener { position ->
                viewer?.updateTransitionImage(imageView)
                overlayView?.update(binding.viewModel!!.galleryModels!![position])
            }.withDismissListener {
                //do any thing when dismiss
                binding.viewModel?.isOpenViewDialog = false
            }.withHiddenStatusBar(false)
            .withImagesMargin(R.dimen.padding_10)
            .withContainerPadding(R.dimen.padding_0)
            .allowZooming(true)
            .allowSwipeToDismiss(true)
            .withOverlayView(overlayView)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodeCaptureActivity && resultCode == RESULT_OK && data != null
        ) {// Tell the media scanner about the new file so that it is
            // immediately available to the user.
            var fileImage = data.extras?.get("ImageFile") as File
            if (fileImage != null) {
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(fileImage.toString()), null
                ) { path, uri ->
                    runOnUiThread {
                        try {
                            // prepare model to send it
                            val customGalleryModel =
                                GalleryModel()
                            customGalleryModel.index_when_selected = 1
                            val itemUrI = DataProvider().getImageURI(
                                applicationContext, fileImage.path
                            )
                            var newUri = Uri.parse(itemUrI)
                            var galleryModel =
                                DataProvider().getLastCapturedGalleryImage(this@MainActivity, newUri)
                            if (galleryModel != null) {
                                //save to data base
                                binding.viewModel?.isOpenViewDialog = true
                                binding.viewModel?.compositeDisposable?.add(Completable.fromCallable {
                                    binding.viewModel?.galleryDAO?.insertGalleryModel(galleryModel)
                                }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        {
                                        }, {

                                        }
                                    ))
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
    }
}