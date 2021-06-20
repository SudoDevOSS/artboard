package com.sudodevoss.core.domain.art.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Media Track General info
 */
@Serializable
data class MediaTrack(
    val name: String,
    @SerialName("listener_count")
    val listenerCount: Int,
    val genres: Array<String>,
    @SerialName("current_track")
    val currentTrack: MediaTrackInfo,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaTrack

        if (name != other.name) return false
        if (listenerCount != other.listenerCount) return false
        if (!genres.contentEquals(other.genres)) return false
        if (currentTrack != other.currentTrack) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + listenerCount
        result = 31 * result + genres.contentHashCode()
        result = 31 * result + currentTrack.hashCode()
        return result
    }

    @Serializable
    data class MediaTrackInfo(
        val title: String,
        @SerialName("artwork_url")
        val artworkUrl: String
    )
}