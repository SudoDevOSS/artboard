package com.sudodevoss.artboard.presentation.components.itemDecorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sudodevoss.artboard.application.extensions.toPx

enum class SpacingOrientation {
    Vertical,
    Horizontal,
    Both
}

/**
 * Custom [RecyclerView] ItemDecoration for adding
 * vertical and horizontal spacing between items
 *
 * @param mSpaceHeight Space in Dp between items
 * @param mOrientation Orientation of space, vertical, horizontal or both
 */
class CustomSpaceDecorator(
    private val mSpaceHeight: Int,
    private val mOrientation: SpacingOrientation
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        when (mOrientation) {
            SpacingOrientation.Horizontal -> {
                setHorizontalSpacing(parent, view, outRect)
            }
            SpacingOrientation.Vertical -> {
                setVerticalSpacing(parent, view, outRect)
            }
            SpacingOrientation.Both -> {
                setHorizontalSpacing(parent, view, outRect)
                setVerticalSpacing(parent, view, outRect)
            }
        }
    }

    private fun setVerticalSpacing(parent: RecyclerView, child: View, outRect: Rect) {
        outRect.bottom = mSpaceHeight.toPx().toInt()
    }

    private fun setHorizontalSpacing(parent: RecyclerView, child: View, outRect: Rect) {
        if (parent.getChildAdapterPosition(child).mod(2) > 0) {
            outRect.left = mSpaceHeight
        }
    }

}