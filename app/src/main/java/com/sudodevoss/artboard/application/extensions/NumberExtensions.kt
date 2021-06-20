package com.sudodevoss.artboard.application.extensions

import android.content.res.Resources
import android.util.TypedValue

/**
 * Convert Dp value to Pixels
 */
fun Number.toPx(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}

/**
 * Convert Pixel value to Dp
 */
fun Number.toDp(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}