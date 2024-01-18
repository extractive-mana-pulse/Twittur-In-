package com.example.twitturin.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.example.twitturin.domain.repository.MyRepository
import com.example.twitturin.helper.SnackbarHelper
import com.example.twitturin.model.network.Api
import com.example.twitturin.model.repo.Repository
import com.example.twitturin.viewmodel.manager.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    @Singleton
//    fun provideMyRepo(api : Api) : MyRepository{
//        return Repository(api)
//    }

    @Provides
    @Singleton
    fun provideSessionManager(app : Application): SessionManager {
        return SessionManager(app)
    }

    @Provides
    @Singleton
    fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Provides
    @Singleton
    fun provideSnackbarHelper(resources: Resources): SnackbarHelper {
        return SnackbarHelper(resources)
    }
}