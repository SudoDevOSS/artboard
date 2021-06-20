package com.sudodevoss.core.data.art.repository

import com.sudodevoss.core.data.art.datasource.ArtDataSource
import com.sudodevoss.core.domain.art.models.MediaTrack
import com.sudodevoss.core.domain.art.repository.MediaTracksRepository

/**
 * Data repository handles calling data source for retreiving data
 */
class MediaTracksRepositoryImpl(private val dataSource: ArtDataSource) : MediaTracksRepository {
    /**
     * Fetch media tracks from server
     *
     * @param searchQuery [String] filter query
     * @param page [Int] number of page to fetch
     * @param limit [Int] number of items to include in response
     * @return [List]<[MediaTrack]> instance
     */
    override fun fetchMediaTracks(
        searchQuery: String,
        page: Int,
        limit: Int
    ): List<MediaTrack> = dataSource.fetchMedia(searchQuery, page, limit)
}