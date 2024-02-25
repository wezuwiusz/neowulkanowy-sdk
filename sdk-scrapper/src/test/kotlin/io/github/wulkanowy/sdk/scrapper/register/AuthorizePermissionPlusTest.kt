package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.exception.VulcanClientError
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.HttpURLConnection
import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class AuthorizePermissionPlusTest : BaseLocalTest() {

    @Test
    fun `authorize with invalid data`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("AuthorizeError.json", responseCode = HttpURLConnection.HTTP_BAD_REQUEST)
        }
        assertFalse(repo.authorizePermission("1234", 111, 222, 333))
        assertEquals(
            Json.encodeToString(
                AuthorizePermissionPlusRequest(
                    key = Base64.encode("111-222-1-333".toByteArray()),
                    pesel = "1234",
                ),
            ),
            server.takeRequest().body.readString(Charset.defaultCharset()),
        )
    }

    @Test(expected = ScrapperException::class)
    fun `authorize with invalid http error`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND))
        }
        repo.authorizePermission("1234", 111, 222, 333)
    }

    @Test(expected = VulcanClientError::class)
    fun `authorize with random error`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue("unknown-error.txt", responseCode = HttpURLConnection.HTTP_BAD_REQUEST)
        }

        repo.authorizePermission("1234", 111, 222, 333)
    }

    @Test
    fun `authorize with valid data`() = runTest {
        val repo = getStudentPlusRepo {
            it.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_NO_CONTENT))
        }
        assertTrue(repo.authorizePermission("82121889474", 12, 23, 34))
        assertEquals(
            Json.encodeToString(
                AuthorizePermissionPlusRequest(
                    key = Base64.encode("12-23-1-34".toByteArray()),
                    pesel = "82121889474",
                ),
            ),
            server.takeRequest().body.readString(Charset.defaultCharset()),
        )
    }
}
