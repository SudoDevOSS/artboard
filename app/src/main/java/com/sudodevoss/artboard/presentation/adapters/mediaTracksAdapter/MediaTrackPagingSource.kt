package com.sudodevoss.artboard.presentation.adapters.mediaTracksAdapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sudodevoss.artboard.application.constants.AppConstants
import com.sudodevoss.core.domain.art.interactors.FetchMediaTrack
import com.sudodevoss.core.domain.art.models.MediaTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MediaTrackPagingSource(
    private val searchQuery: String?,
    private val fetcher: FetchMediaTrack
) : PagingSource<Int, MediaTrack>() {
    override fun getRefreshKey(state: PagingState<Int, MediaTrack>): Int? {
        return state.anchorPosition?.let {
            val page = state.closestPageToPosition(it)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaTrack> {
        return try {
            val nextPage = params.key ?: 0
            if (nextPage > 5) { // For demo. Else check remote mediator for end of fetch.
                return LoadResult.Page(data = listOf(), prevKey = null, nextKey = null)
            }
            delay(1000) // For demo since mock API is too fast
            withContext(Dispatchers.IO) {
                val mediaTracks = fetcher(
                    searchQuery ?: "", nextPage,
                    AppConstants.PAGINATION_PAGE_SIZE
                )
                return@withContext LoadResult.Page(
                    data = mediaTracks,
                    prevKey = null,
                    nextKey = nextPage.plus(1)
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}