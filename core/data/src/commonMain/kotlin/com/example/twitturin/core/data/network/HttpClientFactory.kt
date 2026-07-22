package com.example.twitturin.core.data.network

import com.example.twitturin.core.domain.auth.SessionSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val API_HOST = "twitturin-api.onrender.com"

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
            // Bearer auth, applied per request so the freshest stored token is always used
            // (Ktor's Auth plugin caches loadTokens, which goes stale across login/logout).
            // Sign-in (`POST /auth`) and sign-up (`POST /users`) are exempt: they are public
            // routes, and sending a leftover token of a deleted account makes the backend's
            // token middleware answer 404 "user not found" — the sign-up "Not found." bug.
            // Never attaches the token to 3rd-party hosts (e.g. the GitHub releases endpoint).
            install(
                createClientPlugin("SessionAuth") {
                    onRequest { request, _ ->
                        if (request.url.host != API_HOST) return@onRequest
                        val segments = request.url.encodedPathSegments.filter { it.isNotEmpty() }
                        val isPublicAuthRoute = request.method == HttpMethod.Post &&
                            (segments.lastOrNull() == "auth" || segments == listOf("api", "users"))
                        if (isPublicAuthRoute) return@onRequest
                        val token = sessionSource.getToken() ?: return@onRequest
                        if (request.headers[HttpHeaders.Authorization] == null) {
                            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
                        }
                    }
                },
            )
            install(Logging) {
                level = LogLevel.INFO
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
