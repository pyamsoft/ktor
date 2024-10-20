/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.server.testing

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*

/**
 * [on] function receiver object
 */
object On

/**
 * [it] function receiver object
 */
object It

/**
 * DSL for creating a test case
 */
@Suppress("UNUSED_PARAMETER")
fun on(comment: String, body: On.() -> Unit) = On.body()

/**
 * DSL function for a test case assertions
 */
@Suppress("UNUSED_PARAMETER")
inline fun On.it(description: String, body: It.() -> Unit) = It.body()

/**
 * Returns a parsed content type from a test response.
 */
fun TestApplicationResponse.contentType(): ContentType {
    val contentTypeHeader = requireNotNull(headers[HttpHeaders.ContentType])
    return ContentType.parse(contentTypeHeader)
}

internal fun CoroutineScope.configureSocketTimeoutIfNeeded(
    timeoutAttributes: HttpTimeout.HttpTimeoutCapabilityConfiguration?,
    job: Job,
    extract: () -> Long
) {
    val socketTimeoutMillis = timeoutAttributes?.socketTimeoutMillis
    if (socketTimeoutMillis != null) {
        socketTimeoutKiller(socketTimeoutMillis, job, extract)
    }
}

internal fun CoroutineScope.socketTimeoutKiller(socketTimeoutMillis: Long, job: Job, extract: () -> Long) {
    val killJob = launch {
        var cur = extract()
        while (job.isActive) {
            delay(socketTimeoutMillis)
            val next = extract()
            if (cur == next) {
                throw io.ktor.network.sockets.SocketTimeoutException("Socket timeout elapsed")
            }
            cur = next
        }
    }
    job.invokeOnCompletion {
        killJob.cancel()
    }
}

@OptIn(InternalAPI::class)
internal fun Throwable.mapToKtor(data: HttpRequestData): Throwable {
    return when {
        this is io.ktor.network.sockets.SocketTimeoutException -> SocketTimeoutException(data, this)
        cause?.rootCause is io.ktor.network.sockets.SocketTimeoutException -> SocketTimeoutException(
            data,
            cause?.rootCause
        )

        else -> this
    }
}
