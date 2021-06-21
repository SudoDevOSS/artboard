package com.sudodevoss

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.paging.ExperimentalPagingApi
import com.sudodevoss.artboard.ioc.DIConfigurator
import com.sudodevoss.core.data.common.SharedNetworking
import org.kodein.di.DI

class App : Application() {
    @ExperimentalPagingApi
    companion object {
        val diContainer = DI {
            importAll(DIConfigurator.diModules)
        }
    }

    override fun onCreate() {
        super.onCreate()
        SharedNetworking.configureClient("https://www.mocky.io/v2/", "")
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
    }
}