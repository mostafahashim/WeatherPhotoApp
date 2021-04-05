package com.weather.photo.posterUtil

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.weather.photo.R
import com.weather.photo.model.GalleryModel
import kotlinx.android.synthetic.main.view_poster_overlay.view.*

class PosterOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_poster_overlay, this)
        setBackgroundColor(Color.TRANSPARENT)
    }

    var onShareClick: (GalleryModel) -> Unit = {}
    fun update(poster: GalleryModel) {
        posterOverlayShareButton.setOnClickListener { onShareClick(poster) }
    }
}