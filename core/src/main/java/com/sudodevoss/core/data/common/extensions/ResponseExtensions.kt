package com.sudodevoss.core.data.common.extensions

import okhttp3.Response

fun Response.getBodyOrThrow(): String {
    return body?.string().let { it } ?: throw NullPointerException("Response body is empty")
}