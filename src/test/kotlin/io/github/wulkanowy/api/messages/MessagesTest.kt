package io.github.wulkanowy.api.messages

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

        assertEquals(5, api.getSentMessages(null, null).blockingGet().size)
    }

    @Test
    fun getMessagesSentTest_emptyUnits() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika-empty.json").readText()))
//        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        assertEquals(5, api.getSentMessages(null, null).blockingGet().size)
    }

    @Test
    fun getMessagesSent_recipientWithDashInName() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        val recipients = api.getSentMessages(null, null).blockingGet()

        assertEquals("Czerwieńska - Kowalska Joanna", recipients[1].recipient)
        assertEquals(95, recipients[1].recipientId)
    }

    @Test
    fun getMessagesSent_recipientWithoutBracket() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        val messages = api.getSentMessages(null, null).blockingGet()

        assertEquals("Czerwieńska - Kowalska Joanna", messages[1].recipient)
        assertEquals(95, messages[2].recipientId)
    }

    @Test
    fun getMessagesSent_recipientWithDashInNameAndEmptyUnits() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika-empty.json").readText()))
//        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        val messages = api.getSentMessages(null, null).blockingGet()

        assertEquals("Czerwieńska - Kowalska Joanna", messages[1].recipient)
        assertEquals(0, messages[1].recipientId)
    }

    @Test
    fun getMessagesSent_multiRecipients() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        val message = api.getSentMessages(null, null).blockingGet()[3]

        assertEquals("Czerwieńska - Kowalska Joanna; Tracz Janusz", message.recipient)
        assertEquals(95, message.recipientId)

        val recipients = message.recipients

        assertEquals("Czerwieńska - Kowalska Joanna", recipients[0].name)
        assertEquals(95, recipients[0].loginId)
        assertEquals("Tracz Janusz", recipients[1].name)
        assertEquals(95, recipients[0].loginId)
    }

    @Test
    fun getMessagesSent_multiRecipientsWithOneBroken() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        val message = api.getSentMessages(null, null).blockingGet()[4]

//        assertEquals("Tracz Janusz; Kowalska Joanna", message.recipient)
        assertEquals("Tracz Janusz", message.recipient)
        assertEquals(18, message.recipientId)

        val recipients = message.recipients

        assertEquals("Tracz Janusz", recipients[0].name)
        assertEquals(18, recipients[0].loginId)

        assertEquals(1, recipients.size)
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
                listOf(Recipient("0", "Kowalski Jan", 0, 0, 2, "hash"))
        ).blockingGet()

        val parser = JsonParser()
        val expected = parser.parse(MessagesTest::class.java.getResource("NowaWiadomosc.json").readText())
        val actual = parser.parse(server.takeRequest().body.readUtf8())

        assertEquals(expected, actual)
    }
}
