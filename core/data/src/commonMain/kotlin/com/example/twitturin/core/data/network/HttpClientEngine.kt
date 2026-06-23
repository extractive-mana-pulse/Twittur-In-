package com.example.twitturin.core.data.network

import io.ktor.client.engine.HttpClientEngine

/** Platform HTTP engine: OkHttp on Android/Desktop, Darwin on iOS (Js engine added with the Web target). */
expect fun httpClientEngine(): HttpClientEngine
