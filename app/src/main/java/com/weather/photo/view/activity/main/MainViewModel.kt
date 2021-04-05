package com.weather.photo.view.activity.main

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.weather.photo.MyApplication
import com.weather.photo.adapter.RecyclerImagesAdapter
import com.weather.photo.model.GalleryModel
import com.weather.photo.model.database.GalleryDAO
import com.weather.photo.observer.OnImagePreviewObserver
import com.weather.photo.view.activity.baseActivity.BaseActivityViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(
    application: MyApplication
) : BaseActivityViewModel(application) {
    lateinit var observer: Observer
    var compositeDisposable = CompositeDisposable()
    val galleryDAO: GalleryDAO = db.galleryDao()

    var isShowLoader = MutableLiveData<Boolean>()
    var isShowNoData = MutableLiveData<Boolean>()

    var galleryModels: ArrayList<GalleryModel>? = ArrayList()

    var recyclerImagesAdapter: RecyclerImagesAdapter

    var isOpenViewDialog = false

    init {
        isShowLoader.value = true
        isShowNoData.value = false

        recyclerImagesAdapter =
            RecyclerImagesAdapter(0.0, galleryModels!!, object : OnImagePreviewObserver {
                override fun onOpenViewer(startPosition: Int, imageView: ImageView) {
                    observer.openViewer(startPosition, imageView)
                }
            })
        getLocalGalleryModels()
    }

    fun updateBooksAdapterColumnWidth(screenWidth: Int) {
        var columnWidth = (150.00 * screenWidth) / 360.00
        recyclerImagesAdapter.setColumnWidthAndRatio(columnWidth)
        recyclerImagesAdapter.notifyDataSetChanged()
    }

    private fun getLocalGalleryModels() {
        compositeDisposable.add(galleryDAO.getGalleryModels()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
            }
            .subscribe(
                { dbCountryList ->
                    isShowLoader.value = false
                    if (dbCountryList.isEmpty()) {
                        isShowNoData.value = true
                    } else {
                        galleryModels = dbCountryList.toCollection(ArrayList())
                        recyclerImagesAdapter.setList(galleryModels!!)
                        isShowNoData.value = false
                        if (isOpenViewDialog) {
                            observer.openViewer(0, ImageView(application.context))
                        }
                    }
                },
                { }
            ))
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun onButtonHomeClicked() {
        observer.onButtonAddClicked()
    }

    interface Observer {
        fun onButtonAddClicked()
        fun openViewer(startPosition: Int, imageView: ImageView)
    }

}