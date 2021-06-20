package com.sudodevoss.artboard.presentation.screens.home

import androidx.lifecycle.*
import com.sudodevoss.artboard.application.constants.AppConstants
import com.sudodevoss.core.domain.art.interactors.FetchMediaTrack
import com.sudodevoss.core.domain.art.models.MediaTrack
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.io.IOException

@ExperimentalCoroutinesApi
@FlowPreview
class HomeViewModel(private val fetchMediaTrack: FetchMediaTrack) : ViewModel() {
    private val mActivityIndicatorVisible = MutableLiveData(false)
    val activityIndicatorVisible: LiveData<Boolean> = mActivityIndicatorVisible
    private val mPageActivityIndicatorVisible = MutableLiveData(false)
    val pageActivityIndicatorVisible: LiveData<Boolean> = mPageActivityIndicatorVisible
    private val mSearchActivityIndicatorVisible = MutableLiveData(false)
    val searchActivityIndicatorVisible: LiveData<Boolean> = mSearchActivityIndicatorVisible
    private val mUiState =
        MutableStateFlow<HomeViewUIState>(HomeViewUIState.Success(null))
    val uiState: StateFlow<HomeViewUIState> = mUiState
    val searchChannel = ConflatedBroadcastChannel<String>()
    val pageChannel = ConflatedBroadcastChannel<Int>()

    private val mPageChannelFlow = pageChannel
        .asFlow()
        .mapLatest {
            try {
                runOnMainThread { mPageActivityIndicatorVisible.value = true }
                loadMore(it)
            } finally {
                runOnMainThread { mPageActivityIndicatorVisible.value = false }
            }
        }
    private val mSearchChannelFlow = searchChannel
        .asFlow()
        .debounce(AppConstants.SEARCH_DELAY_MILLIS)
        .mapLatest {
            runOnMainThread { mSearchActivityIndicatorVisible.value = true }
            try {
                when {
                    it.length >= AppConstants.MIN_SEARCH_QUERY_LENGTH -> {
                        searchMediaTracks(it)
                    }
                    it.isEmpty() -> {
                        loadMediaTracks()
                    }
                    else -> {
                        emptyList<MediaTrack>()
                    }
                }
            } finally {
                runOnMainThread { mSearchActivityIndicatorVisible.value = false }
            }
        }

    private val mMediaTracksFlow =
        merge(
            flow {
                runOnMainThread { mActivityIndicatorVisible.value = true }
                val tracks = loadMediaTracks()
                runOnMainThread { mActivityIndicatorVisible.value = false }
                emit(tracks)
            },
            mPageChannelFlow,
            mSearchChannelFlow
        )
            .retry { e ->
                val shouldRetry = e is IOException
                if (shouldRetry) delay(1000)
                shouldRetry
            }
            .catch { e -> mUiState.value = HomeViewUIState.Error(e) }
            .flowOn(Dispatchers.IO)
            .shareIn(viewModelScope, SharingStarted.Lazily)
            .mapLatest {
                mUiState.value = HomeViewUIState.Success(null)
                it
            }


    val mediaTracks = mMediaTracksFlow.asLiveData()

    private fun loadMore(page: Int) = loadMediaTracks(page = page)
    private fun searchMediaTracks(query: String) = loadMediaTracks(query)
    private fun loadMediaTracks(searchQuery: String = "", page: Int = 0): List<MediaTrack> {
        return fetchMediaTrack(
            searchQuery,
            page,
            AppConstants.PAGINATION_PAGE_SIZE
        )
    }

    private fun runOnMainThread(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            block()
        }
    }
}