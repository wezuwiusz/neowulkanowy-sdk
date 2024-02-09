package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.login.InvalidSymbolException
import io.github.wulkanowy.sdk.scrapper.service.SymbolService

internal class SymbolRepository(
    private val symbolService: SymbolService,
) {

    suspend fun isSymbolNotExist(symbol: String): Boolean {
        val res = runCatching { symbolService.getSymbolPage(symbol) }

        when (res.exceptionOrNull()) {
            is InvalidSymbolException -> return true
            else -> res.getOrThrow()
        }

        // can't tell
        return false
    }
}
