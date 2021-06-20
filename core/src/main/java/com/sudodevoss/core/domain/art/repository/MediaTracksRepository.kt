package com.sudodevoss.core.domain.art.repository

import com.sudodevoss.core.domain.art.models.MediaTrack

interface MediaTracksRepository {
    /**
     * Fetch media tracks from server
     *
     * @param searchQuery [String] filter query
     * @param page [Int] number of page to fetch
     * @param limit [Int] number of items to include in response
     * @return [List]<[MediaTrack]> instance
     */
    fun fetchMediaTracks(searchQuery: String, page: Int, limit: Int): List<MediaTrack>
}