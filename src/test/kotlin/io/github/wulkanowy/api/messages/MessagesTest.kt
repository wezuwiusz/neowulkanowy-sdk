package io.github.wulkanowy.api.messages

import io.github.wulkanowy.api.BaseTest
import io.github.wulkanowy.api.repository.MessagesRepository
import io.github.wulkanowy.api.service.MessagesService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MessagesTest : BaseTest() {

    private val api by lazy {
        MessagesRepository(1, getService(MessagesService::class.java, "http://fakelog.localhost:3000/", false))
    }

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
    }

    @Test
    fun getReceivedMessagesTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciOdebrane.json").readText()))
        server.start(3000)

        assertEquals(2, api.getReceivedMessages(null, null).blockingGet().size)
    }

    @Test
    fun getDeletedMessagesTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciUsuniete.json").readText()))
        server.start(3000)

        assertEquals(1, api.getDeletedMessages(null, null).blockingGet().size)
    }

    @Test
    fun getMessagesSentTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        assertEquals(1, api.getSentMessages(null, null).blockingGet().size)
    }

    @Test
    fun getMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Wiadomosc.json").readText()))
        server.start(3000)

        assertEquals(27214, api.getMessage(1, 1).blockingGet().id)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}
