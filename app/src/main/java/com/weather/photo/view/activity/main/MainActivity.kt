package com.weather.photo.view.activity.main

import android.content.ContentUris
import android.content.Intent
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
        var screenWidth: Int = ScreenSizeUtils().getScreenWidth(this)
        binding.viewModel!!.updateBooksAdapterColumnWidth(screenWidth)
        setHomeButtonVisibility(true)
    }

    fun setHomeButtonVisibility(isVisible: Boolean) {
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
                            this@MainActivity,context.getString(R.string.cant_find_the_image_file),
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

    private fun getLastCapturedGalleryImage(uri: Uri): GalleryModel? {
        try {
            val columnsImages = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATE_ADDED,
                "bucket_id",
                "bucket_display_name",
                MediaStore.Images.Media._ID
            )
            val orderByimage = MediaStore.Images.Media.DATE_MODIFIED
            val imagecursor = contentResolver.query(
                uri,
                columnsImages, null, null, "$orderByimage DESC"
            )
            if (imagecursor != null && imagecursor.count > 0) {
                imagecursor.moveToFirst()
                val item = GalleryModel()
                item.index_when_selected = 1
                item.isSeleted = true
                // get path
                val dataColumnIndex = imagecursor
                    .getColumnIndex(MediaStore.Images.Media.DATA)
                item.sdcardPath = imagecursor.getString(dataColumnIndex)
                // get date modified
                var dateColumnIndex = imagecursor
                    .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
                var Image_date = imagecursor.getString(dateColumnIndex)
                if (Image_date != null) {
                    item.item_date_modified = Integer.parseInt(Image_date)
                } else {
                    dateColumnIndex = imagecursor
                        .getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                    Image_date = imagecursor.getString(dateColumnIndex)
                    if (Image_date != null) {
                        item.item_date_modified = Integer.parseInt(Image_date)
                    } else {
                        item.item_date_modified = SystemClock.elapsedRealtime().toInt()
                    }
                }
                // get uri
                // content://media/external/images/media/19490
                val imageuri = ContentUris
                    .withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imagecursor.getInt(
                            imagecursor
                                .getColumnIndex(MediaStore.Images.Media._ID)
                        ).toLong()
                    )
                item.itemUrI = imageuri.toString()
                //get albumName
                val albumNameColumnIndex = imagecursor
                    .getColumnIndex("bucket_display_name")
                item.albumName = imagecursor.getString(albumNameColumnIndex)
                return item
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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
                            var galleryModel = getLastCapturedGalleryImage(newUri)
                            if (galleryModel != null) {
                                /*if (binding.viewModel?.galleryModels.isNullOrEmpty()) {
                                    binding.viewModel?.galleryModels = ArrayList()
                                    binding.viewModel?.galleryModels?.add(galleryModel)
                                } else {
                                    binding.viewModel?.galleryModels?.add(0, galleryModel)
                                }
                                binding.viewModel?.recyclerImagesAdapter?.notifyDataSetChanged()*/
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