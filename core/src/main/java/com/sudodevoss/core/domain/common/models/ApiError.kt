package com.sudodevoss.core.domain.common.models

sealed class ApiError(override val message: String) : Throwable(message) {
    data class NetworkConnection(override val message: String) : ApiError(message)
    data class BadRequest(override val message: String) : ApiError(message)
    data class UnAuthorized(override val message: String) : ApiError(message)
    data class InternalServerError(override val message: String) : ApiError(message)
    data class ResourceNotFound(override val message: String) : ApiError(message)
}