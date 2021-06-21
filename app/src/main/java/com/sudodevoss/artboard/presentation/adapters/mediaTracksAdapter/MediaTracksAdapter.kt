package com.sudodevoss.artboard.presentation.adapters.mediaTracksAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudodevoss.artboard.R
import com.sudodevoss.artboard.application.comparators.MediaTrackComparator
import com.sudodevoss.artboard.utils.imageLoader.ImageLoader
import com.sudodevoss.core.domain.art.models.MediaTrack

class MediaTracksAdapter(
    private val mMediaTracks: MutableList<MediaTrack>,
    private val mImageLoader: ImageLoader
) :
    PagingDataAdapter<MediaTrack, MediaTracksAdapter.ViewHolder>(MediaTrackComparator) {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.clipToOutline = true
        }

        val mediaArt: ImageView = view.findViewById(R.id.trackImage)
        val mediaName: TextView = view.findViewById(R.id.trackName)
        val generes: TextView = view.findViewById(R.id.trackGeneres)
        val playCount: TextView = view.findViewById(R.id.txtPlayCount)
    }

    /**
     * Update media track list and notify listeners about data change
     *
     * @param mediaTracks [List]<[MediaTrack]> newly available list
     * @param reset [Boolean] if true [mMediaTracks] is cleared before appending new list
     */
    fun update(mediaTracks: List<MediaTrack>, reset: Boolean = false) {
        if (reset) {
            mMediaTracks.clear()
        }
        mMediaTracks.addAll(mediaTracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.media_track, parent, false)
        val size = parent.measuredWidth / 2 - 48
        view.layoutParams = GridLayoutManager.LayoutParams(size, size)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTrack = getItem(position)
        holder.mediaName.text = currentTrack?.name ?: ""
        holder.generes.text = currentTrack?.genres?.joinToString(", ") ?: ""
        holder.playCount.text = currentTrack?.listenerCount.toString() ?: ""
        if (currentTrack != null) {
            mImageLoader.load(currentTrack.currentTrack.artworkUrl, holder.mediaArt)
        }
    }
}