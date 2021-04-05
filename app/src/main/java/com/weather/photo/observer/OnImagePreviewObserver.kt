package com.weather.photo.observer

import android.widget.ImageView

interface OnImagePreviewObserver {
    fun onOpenViewer(startPosition: Int, imageView: ImageView)
}