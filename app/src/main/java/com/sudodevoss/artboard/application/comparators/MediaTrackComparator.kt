package com.sudodevoss.artboard.application.comparators

import androidx.recyclerview.widget.DiffUtil
import com.sudodevoss.core.domain.art.models.MediaTrack

object MediaTrackComparator : DiffUtil.ItemCallback<MediaTrack>() {
    override fun areItemsTheSame(oldItem: MediaTrack, newItem: MediaTrack): Boolean {
        // Name is unique.
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: MediaTrack, newItem: MediaTrack): Boolean {
        return oldItem == newItem
    }
}
