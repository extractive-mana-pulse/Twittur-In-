package com.example.twitturin.core.data.network

import com.example.twitturin.core.domain.util.DataError
import com.example.twitturin.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/** Base API URL (legacy backend). Kept here so [constructRoute] works in commonMain. */
internal const val BASE_URL = "https://twitturin-api.onrender.com/api/"

suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf(),
): Result<Response, DataError.Network> = safeCall {
    get {
        url(constructRoute(route))
        queryParameters.forEach { (key, value) -> parameter(key, value) }
    }
}

suspend inline fun <reified Request, reified Response : Any> HttpClient.post(
    route: String,
    body: Request,
): Result<Response, DataError.Network> = safeCall {
    post {
        url(constructRoute(route))
        setBody(body)
    }
}

/**
 * Body-less POST (e.g. `POST following/{id}` — the action is keyed by the URL + bearer token,
 * the server ignores any body). Type the [Response] as `Unit` for fire-and-forget writes.
 */
suspend inline fun <reified Response : Any> HttpClient.post(
    route: String,
): Result<Response, DataError.Network> = safeCall {
    post {
        url(constructRoute(route))
    }
}

suspend inline fun <reified Request, reified Response : Any> HttpClient.put(
    route: String,
    body: Request,
): Result<Response, DataError.Network> = safeCall {
    put {
        url(constructRoute(route))
        setBody(body)
    }
}

suspend inline fun <reified Response : Any> HttpClient.delete(
    route: String,
    queryParameters: Map<String, Any?> = mapOf(),
): Result<Response, DataError.Network> = safeCall {
    delete {
        url(constructRoute(route))
        queryParameters.forEach { (key, value) -> parameter(key, value) }
    }
}

/** DELETE with a request body (e.g. `DELETE tweets/{id}/likes` whose body is the new count). */
suspend inline fun <reified Request, reified Response : Any> HttpClient.delete(
    route: String,
    body: Request,
): Result<Response, DataError.Network> = safeCall {
    delete {
        url(constructRoute(route))
        setBody(body)
    }
}

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        return Result.Error(DataError.Network.SERIALIZATION)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        return Result.Error(DataError.Network.UNKNOWN)
    }
    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> {
    return when (response.status.value) {
        in 200..299 -> Result.Success(response.body<T>())
        400 -> Result.Error(DataError.Network.BAD_REQUEST)
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        403 -> Result.Error(DataError.Network.FORBIDDEN)
        404 -> Result.Error(DataError.Network.NOT_FOUND)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

fun constructRoute(route: String): String = when {
    // Absolute URLs (e.g. the GitHub releases API) bypass the base URL entirely.
    route.startsWith("http://") || route.startsWith("https://") -> route
    route.contains(BASE_URL) -> route
    route.startsWith("/") -> BASE_URL + route.drop(1)
    else -> BASE_URL + route
}
