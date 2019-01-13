package io.github.wulkanowy.api.messages

import com.google.gson.Gson
import com.google.gson.JsonParser
import io.github.wulkanowy.api.BaseLocalTest
import io.github.wulkanowy.api.repository.MessagesRepository
import io.github.wulkanowy.api.service.MessagesService
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class MessagesTest : BaseLocalTest() {

    private val api by lazy {
        MessagesRepository(getService(MessagesService::class.java, "http://fakelog.localhost:3000/", false))
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
    fun getMessagesSentTest_emptyUnits() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika-empty.json").readText()))
//        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        assertEquals(1, api.getSentMessages(null, null).blockingGet().size)
    }

    @Test
    fun getMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Wiadomosc.json").readText()))
        server.start(3000)

        assertEquals(90, api.getMessage(1, 1, false, 0).blockingGet().length)
    }

    @Test
    fun sendMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WyslanaWiadomosc.json").readText()))
        server.start(3000)

        api.sendMessage("Temat wiadomości", "Tak wygląda zawartość wiadomości.\nZazwyczaj ma wiele linijek.\n\nZ poważaniem,\nNazwisko Imię",
                listOf(Recipient("0", "Kowalski Jan", 0, 0, 2, "hash"))).blockingGet()

        val parser = JsonParser()
        val expected = parser.parse(MessagesTest::class.java.getResource("NowaWiadomosc.json").readText())
        val actual = parser.parse(server.takeRequest().body.readUtf8())

        assertEquals(expected, actual)
    }
}
