package com.sudodevoss.core.data.art.datasource

import com.sudodevoss.core.data.common.SharedNetworking
import com.sudodevoss.core.data.common.extensions.getBodyOrThrow
import com.sudodevoss.core.domain.art.models.MediaTrack
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Represents remote data source to be reached over network
 */
class RemoteArtDataSource : ArtDataSource {
    private val endpointUrl = "5df79a3a320000f0612e0115"

    /**
     * Fetch media tracks from data source
     *
     * @param searchQuery [String] filtering query
     * @param page [Int] page number
     * @param limit [Int] Number of items to include in result
     */
    @Throws(Throwable::class)
    override fun fetchMedia(
        searchQuery: String,
        page: Int,
        limit: Int
    ): List<MediaTrack> {
        var url = "$endpointUrl?page=$page&limit=$limit"
        if (searchQuery.isNotEmpty()) {
//            url = "$url&q=$searchQuery"
            url = "5df79b1f320000f4612e011e"
        }
        val request = SharedNetworking.newRequestBuilder(url).build()
        val response = SharedNetworking.httpClient!!.newCall(request).execute()

        if (!response.isSuccessful) {
            throw SharedNetworking.parseNonSuccessHttpStatusCode(response.code)
        }

        val body = response.getBodyOrThrow()
        val parsedBody = Json.decodeFromString<Map<String, Map<String, List<MediaTrack>>>>(body)
        return if (parsedBody.containsKey("data") && parsedBody["data"]!!.contains("sessions")) {
            parsedBody["data"]!!["sessions"]?.shuffled() ?: emptyList()
        } else {
            emptyList()
        }
    }
}