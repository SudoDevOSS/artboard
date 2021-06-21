package com.sudodevoss.artboard.presentation.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sudodevoss.artboard.application.constants.AppConstants
import com.sudodevoss.artboard.presentation.adapters.mediaTracksAdapter.MediaTrackPagingSource
import com.sudodevoss.artboard.presentation.adapters.mediaTracksAdapter.MediaTrackRemoteMediator
import com.sudodevoss.core.domain.art.interactors.FetchMediaTrack
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.io.IOException

@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalPagingApi
class HomeViewModel(private val fetchMediaTrack: FetchMediaTrack) : ViewModel() {
    val pageActivityIndicatorVisible = MutableLiveData(false)
    val searchActivityIndicatorVisible = MutableLiveData(false)
    val searchChannel = ConflatedBroadcastChannel<String>()
    private var mSearchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = mSearchQuery

    private val mMediaTrackPagingSource = Pager(
        config = PagingConfig(pageSize = AppConstants.PAGINATION_PAGE_SIZE),
        remoteMediator = MediaTrackRemoteMediator()
    ) { MediaTrackPagingSource(mSearchQuery.value, fetchMediaTrack) }

    init {
        viewModelScope.launch {
            searchChannel.asFlow().debounce(AppConstants.SEARCH_DELAY_MILLIS).collect {
                if (it.length >= AppConstants.MIN_SEARCH_QUERY_LENGTH) {
                    mSearchQuery.value = it
                } else if (it.isEmpty()) {
                    mSearchQuery.value = ""
                }
            }
        }
    }

    val mediaTracksSteamFlow = mMediaTrackPagingSource
        .flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)
        .retry { e ->
            val shouldRetry = e is IOException
            if (shouldRetry) delay(1000)
            shouldRetry
        }
        .flowOn(Dispatchers.IO)
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    private fun runOnMainThread(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            block()
        }
    }
}