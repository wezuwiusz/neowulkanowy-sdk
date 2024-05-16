package io.github.wulkanowy.sdk.scrapper

import java.io.Closeable

interface EvaluateHandler : Closeable {

    suspend fun evaluate(code: String): String? = null

    override fun close() = Unit
}
