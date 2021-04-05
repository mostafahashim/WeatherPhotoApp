package com.weather.photo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weather.photo.R
import com.weather.photo.databinding.ItemRecyclerImageUploadBinding
import com.weather.photo.model.GalleryModel
import com.weather.photo.observer.OnImagePreviewObserver
import hk.ids.gws.android.sclick.SClick

class RecyclerImagesAdapter(
    var columnWidth: Double,
    var galleryModels: ArrayList<GalleryModel>,
    var onImagePreviewObserver: OnImagePreviewObserver
) : RecyclerView.Adapter<RecyclerImagesAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerImagesAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_recycler_image_upload,
            parent,
            false
        ) as ItemRecyclerImageUploadBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = galleryModels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.layoutItemContainer.layoutParams.width = columnWidth.toInt()
        holder.binding.layoutItemContainer.layoutParams.height = columnWidth.toInt()
        holder.binding.layoutItemContainer.requestLayout()

        Glide.with(context).load("file://" + galleryModels[holder.adapterPosition].sdcardPath)
            .into(holder.binding.ivItemRecycler)


        holder.binding.ivItemRecycler.setOnClickListener {
            if (SClick.check(SClick.BUTTON_CLICK)) {
                onImagePreviewObserver.onOpenViewer(
                    holder.adapterPosition,
                    holder.binding.ivItemRecycler
                )
            }
        }


    }

    fun setList(list: ArrayList<GalleryModel>) {
        this.galleryModels = list
        notifyDataSetChanged()
    }

    fun setColumnWidthAndRatio(columnWidth: Double) {
        this.columnWidth = columnWidth
    }

    class ViewHolder(var binding: ItemRecyclerImageUploadBinding) :
        RecyclerView.ViewHolder(binding.root)


}