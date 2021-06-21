package com.sudodevoss.artboard.presentation.adapters.mediaTracksAdapter

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.sudodevoss.core.domain.art.models.MediaTrack

@ExperimentalPagingApi
class MediaTrackRemoteMediator : RemoteMediator<Int, MediaTrack>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MediaTrack>
    ): MediatorResult {
        return when (loadType) {
            LoadType.REFRESH -> MediatorResult.Success(false)
            LoadType.PREPEND -> MediatorResult.Success(false)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null || state.pages.size > 5) {
                    return MediatorResult.Success(true)
                }
                return MediatorResult.Success(false)
            }
        }
    }
}