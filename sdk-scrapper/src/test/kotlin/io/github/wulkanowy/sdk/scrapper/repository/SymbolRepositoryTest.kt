package io.github.wulkanowy.sdk.scrapper.repository

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptorTest
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.register.RegisterTest
import io.github.wulkanowy.sdk.scrapper.service.SymbolService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SymbolRepositoryTest : BaseLocalTest() {

    private val symbolRepository by lazy {
        SymbolRepository(
            getService(
                service = SymbolService::class.java,
                okHttp = getOkHttp(errorInterceptor = true, autoLoginInterceptorOn = false),
            ),
        )
    }

    @Test(expected = ServiceUnavailableException::class)
    fun `check symbol when vulcan has maintenance break`() = runTest {
        server.enqueue("AktualizacjaBazyDanych.html", ErrorInterceptorTest::class.java)

        symbolRepository.isSymbolNotExist("warszawa")
    }

    @Test
    fun `check symbol when it exist`() = runTest {
        server.enqueue("LoginPage-standard.html", LoginTest::class.java)

        assertFalse(symbolRepository.isSymbolNotExist("warszawa"))
    }

    @Test
    fun `check symbol when it not exist`() = runTest {
        server.enqueue("InvalidSymbol.html", RegisterTest::class.java)

        assertTrue(symbolRepository.isSymbolNotExist("test"))
    }
}
