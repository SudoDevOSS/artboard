package com.sudodevoss.core.data.common

import com.sudodevoss.core.domain.common.models.ApiError
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit


/**
 * Provides shared [OkHttpClient] and [Request] builder
 */
object SharedNetworking {
    /**
     * API base url
     */
    private var baseUrl: String = ""

    /**
     * Shared [OkHttpClient] client with default configuration
     *
     * call/connect/read/write Timeout: 15 seconds
     * retry on connection failure
     */
    private fun defaultBuilder(): OkHttpClient.Builder {
        val timeout = 15L
        return OkHttpClient.Builder()
            .callTimeout(timeout, TimeUnit.SECONDS)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
    }

    /**
     * Shared [OkHttpClient] instance
     *
     * @throws NullPointerException if [configureClient] not called before attempting to access this instance
     */
    var httpClient: OkHttpClient? = null
        private set
        get() {
            if (field == null) {
                throw NullPointerException("httpClient is not initialized")
            }
            return field
        }

    /**
     * Initialize new [Request.Builder] instance with url composed of [baseUrl] + [endpointUrl]
     *
     * @param endpointUrl API Endpoint path
     * @return [Request.Builder] instance
     */
    fun newRequestBuilder(endpointUrl: String): Request.Builder {
        if (baseUrl.isEmpty()) {
            throw NullPointerException("Base URL not configured. Call BaseNetworking::buildClient at least once before calling this method")
        }
        return Request.Builder().url("$baseUrl$endpointUrl")
    }

    /**
     * Configures shared [OkHttpClient] instance and sets [baseUrl]
     * If [authorizationToken] is passed [httpClient] is initialized with [Authenticator]
     * instance for Authorization header
     */
    fun configureClient(url: String, authorizationToken: String) {
        val httpClientBuilder = defaultBuilder()
        if (authorizationToken.isNotBlank()) {
            httpClientBuilder.authenticator(Authenticator(authorizationToken))
        }

        if (url.isEmpty()) {
            throw IllegalArgumentException("Base URL cannot be empty")
        }

        baseUrl = url
        httpClient = httpClientBuilder.build()
    }

    /**
     * Parse status code and returns correct exception
     *
     * @return [Throwable]
     */
    fun parseNonSuccessHttpStatusCode(code: Int): Throwable {
        return when (code) {
            400 -> ApiError.BadRequest("")
            401, 403 -> ApiError.UnAuthorized("")
            404 -> ApiError.ResourceNotFound("")
            in 500..599 -> ApiError.InternalServerError("")
            else -> Exception("$code")
        }
    }

    /**
     * Authenticator instance for adding Authorization header to all requests that are not part of the following
     * 1. Login
     * 2. Register
     * 3. Forgot password
     *
     * Added to demonstrate authentication handling
     */
    private class Authenticator(private val token: String) : okhttp3.Authenticator {
        private val nonAuthenticatedUrls = arrayListOf("login", "register", "forgot_password")

        override fun authenticate(route: Route?, response: Response): Request? {
            val shouldAddAuthHeader =
                nonAuthenticatedUrls.none { routesUrl ->
                    val path = (route?.address?.url?.encodedPath.let { it } ?: "")
                    routesUrl in path
                }

            return if (shouldAddAuthHeader) {
                val newRequest = response.request.newBuilder().apply {
                    this.addHeader("Authorization", "Bearer $token")
                }
                newRequest.build()
            } else {
                response.request
            }
        }
    }
}