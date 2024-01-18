package com.example.twitturin.domain.repository

interface MyRepository {
    suspend fun doNetworkCall()
}