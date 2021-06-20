package com.sudodevoss.core.data.common.extensions

fun <T> T?.unwrapOrThrowIfNull(errorMessage: String? = null): T {
    if (this == null) {
        throw NullPointerException(errorMessage)
    }
    return this
}