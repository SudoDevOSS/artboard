package com.sudodevoss.artboard.utils.imageLoader

import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageLoaderImpl : ImageLoader {
    override fun load(resource: String, target: ImageView) {
        Glide.with(target).load(resource).into(target)
    }
}