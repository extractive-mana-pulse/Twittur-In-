package com.example.twitturin.core.data.network

import com.example.twitturin.core.domain.auth.SessionSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Configures the HttpClient once. Engine is injected so tests can pass a MockEngine. */
object HttpClientFactory {
    fun create(engine: HttpClientEngine, sessionSource: SessionSource): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        sessionSource.getToken()?.let { token -> BearerTokens(token, refreshToken = "") }
                    }
                }
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
