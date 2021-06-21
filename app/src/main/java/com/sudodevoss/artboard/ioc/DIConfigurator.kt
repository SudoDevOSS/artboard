package com.sudodevoss.artboard.ioc

import androidx.paging.ExperimentalPagingApi
import com.sudodevoss.artboard.presentation.screens.home.HomeViewModel
import com.sudodevoss.artboard.utils.imageLoader.ImageLoader
import com.sudodevoss.artboard.utils.imageLoader.ImageLoaderImpl
import com.sudodevoss.core.data.art.datasource.ArtDataSource
import com.sudodevoss.core.data.art.datasource.RemoteArtDataSource
import com.sudodevoss.core.data.art.interactors.FetchMediaTracksImpl
import com.sudodevoss.core.data.art.repository.MediaTracksRepositoryImpl
import com.sudodevoss.core.domain.art.interactors.FetchMediaTrack
import com.sudodevoss.core.domain.art.repository.MediaTracksRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

@ExperimentalPagingApi
object DIConfigurator {
    private val mediaTracksDIModule = DI.Module("mediaTracks") {
        val dataSourceTag = "remoteDataSource"
        bind<FetchMediaTrack>() with provider { FetchMediaTracksImpl(instance()) }
        bind<MediaTracksRepository>() with provider {
            MediaTracksRepositoryImpl(instance(tag = dataSourceTag))
        }
        bind<ArtDataSource>(tag = dataSourceTag) with provider { RemoteArtDataSource() }
    }

    private val mediaTracksViewModelsDIModule = DI.Module("mediaTracksViewModels") {
        bind<HomeViewModel>() with provider { HomeViewModel(instance()) }
        bind<ImageLoader>() with provider { ImageLoaderImpl() }
    }

    val diModules = listOf(mediaTracksViewModelsDIModule, mediaTracksDIModule)
}