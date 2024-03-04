package com.example.twitturin.auth.di

import com.example.twitturin.auth.model.domain.repository.AuthRepository
import com.example.twitturin.auth.model.domain.repositoryImpl.AuthRepositoryImpl
import com.example.twitturin.auth.model.network.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi) : AuthRepository {
        return AuthRepositoryImpl(authApi)
    }
}