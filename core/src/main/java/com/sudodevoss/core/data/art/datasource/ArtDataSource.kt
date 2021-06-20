package com.sudodevoss.core.data.art.datasource

import com.sudodevoss.core.domain.art.models.MediaTrack

/**
 * Data source contract defines methods used
 * for interacting with source of data, e.g. server, local db
 */
interface ArtDataSource {
    /**
     * Fetch media tracks from data source
     *
     * @param searchQuery [String] filtering query
     * @param page [Int] page number
     * @param limit [Int] Number of items to include in result
     */
    fun fetchMedia(
        searchQuery: String,
        page: Int,
        limit: Int
    ): List<MediaTrack>
}