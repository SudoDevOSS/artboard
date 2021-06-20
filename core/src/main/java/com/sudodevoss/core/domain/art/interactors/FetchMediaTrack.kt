package com.sudodevoss.core.domain.art.interactors

import com.sudodevoss.core.domain.art.models.MediaTrack

/**
 * Fetch media tracks from data source
 */
interface FetchMediaTrack {
    operator fun invoke(
        searchQuery: String,
        page: Int, limit: Int
    ): List<MediaTrack>
}