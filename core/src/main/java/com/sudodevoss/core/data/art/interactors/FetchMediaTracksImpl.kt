package com.sudodevoss.core.data.art.interactors

import com.sudodevoss.core.domain.art.interactors.FetchMediaTrack
import com.sudodevoss.core.domain.art.repository.MediaTracksRepository

/**
 * Fetch media tracks from data source
 *
 * @param mediaTracksRepository [MediaTracksRepository]
 */
class FetchMediaTracksImpl(private val mediaTracksRepository: MediaTracksRepository) :
    FetchMediaTrack {
    override operator fun invoke(
        searchQuery: String,
        page: Int, limit: Int
    ) = mediaTracksRepository.fetchMediaTracks(searchQuery, page, limit)
}