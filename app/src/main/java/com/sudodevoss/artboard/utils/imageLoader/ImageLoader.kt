package com.sudodevoss.artboard.utils.imageLoader

import android.widget.ImageView

interface ImageLoader {
    fun load(resource: String, target: ImageView)
}