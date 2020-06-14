package io.github.wulkanowy.sdk.mobile.interceptor

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.exception.InvalidSymbolException
import io.github.wulkanowy.sdk.mobile.repository.RegisterRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.create
import java.io.IOException

class ErrorInterceptorTest : BaseLocalTest() {

    @Test
    fun unknownError() {
        server.enqueueAndStart("bad-request.txt")

        val repo = RegisterRepository(getRetrofitBuilder().baseUrl("http://localhost:3030/").build().create())

        try {
            runBlocking { repo.getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is IOException)
        }
    }

    @Test
    fun invalidSymbol_diacritics() {
        server.enqueueAndStart("invalid-symbol.html")

        val repo = RegisterRepository(getRetrofitBuilder().baseUrl("http://localhost:3030/").build().create())

        try {
            runBlocking { repo.getStudents() }
        } catch (e: Throwable) {
            assertTrue(e is InvalidSymbolException)
        }
    }
}
