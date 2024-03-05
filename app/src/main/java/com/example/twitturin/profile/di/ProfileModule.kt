package com.example.twitturin.profile.di

import com.example.twitturin.profile.domain.repository.ProfileRepository
import com.example.twitturin.profile.domain.repositoryImpl.ProfileRepositoryImpl
import com.example.twitturin.profile.model.network.ProfileApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileApi: ProfileApi) : ProfileRepository {
        return ProfileRepositoryImpl(profileApi)
    }
}