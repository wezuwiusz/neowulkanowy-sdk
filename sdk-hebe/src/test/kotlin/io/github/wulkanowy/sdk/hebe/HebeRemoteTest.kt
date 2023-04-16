package io.github.wulkanowy.sdk.hebe

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)
class HebeRemoteTest {

    private val hebe = Hebe()

    @Before
    fun setUp() {
        with(hebe) {
            logLevel = HttpLoggingInterceptor.Level.BODY
            keyId = ""
            privatePem = ""
            baseUrl = ""
            schoolSymbol = ""
            deviceModel = "Pixel 4a (5G)"
        }
    }

    @Test
    fun `register device`() = runTest {
        val res = hebe.register(
            firebaseToken = "###",
            token = "FK11234",
            pin = "12334",
            symbol = "powiatwulkanowy",
        )
        println(res)
    }
}
