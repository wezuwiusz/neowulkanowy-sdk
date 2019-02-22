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

        assertEquals(6, api.getSentMessages(null, null).blockingGet().size)
    }

    @Test
    fun getMessagesSentTest_emptyUnits() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika-empty.json").readText()))
//        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        assertEquals(6, api.getSentMessages(null, null).blockingGet().size)
    }

    @Test
    fun getMessagesSent_recipientWithDashInName() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[1].run {
            assertEquals("Czerwieńska - Kowalska Joanna", recipient)
            assertEquals(95, recipientId)
        }
    }

    @Test
    fun getMessagesSent_recipientWithoutBracket() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet().run {
            assertEquals("Czerwieńska - Kowalska Joanna", this[1].recipient)
            assertEquals(95, this[2].recipientId)
        }
    }

    @Test
    fun getMessagesSent_recipientWithDashInNameAndEmptyUnits() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika-empty.json").readText()))
//        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[1].run {
            assertEquals("Czerwieńska - Kowalska Joanna", recipient)
            assertEquals(0, recipientId)
        }
    }

    @Test
    fun getMessagesSent_multiRecipients() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[3].run {
            assertEquals("Czerwieńska - Kowalska Joanna; Tracz Janusz", recipient)
            assertEquals(95, recipientId)

            assertEquals("Czerwieńska - Kowalska Joanna", recipients[0].name)
            assertEquals(95, recipients[0].loginId)
            assertEquals("Tracz Janusz", recipients[1].name)
            assertEquals(95, recipients[0].loginId)
        }
    }

    @Test
    fun getMessagesSent_multiRecipientsWithOneBroken() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[4].run {
            assertEquals("Tracz Janusz; Kowalska Joanna", recipient)
            assertEquals(18, recipientId)

            assertEquals(2, recipients.size)
            assertEquals("Tracz Janusz", recipients[0].name)
        }
    }

    @Test
    fun getMessagesSent_multiRecipientsWithAllBroken() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("JednostkiUzytkownika.json").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[5].run {
            assertEquals("Tracz Antoni (TA); Kowalska Joanna", recipient)
            assertEquals(0, recipientId)

            assertEquals(2, recipients.size)
            assertEquals("Tracz Antoni (TA)", recipients[0].name)
            assertEquals(0, recipients[0].loginId)
            assertEquals("Kowalska Joanna", recipients[1].name)
            assertEquals(0, recipients[1].loginId)
        }
    }

    @Test
    fun getMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Wiadomosc.json").readText()))
        server.start(3000)

        assertEquals(90, api.getMessage(1, 1, false, 0).blockingGet().length)
    }

    @Test
    fun sendMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Start.html").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WyslanaWiadomosc.json").readText()))
        server.start(3000)

        api.sendMessage("Temat wiadomości", "Tak wygląda zawartość wiadomości.\nZazwyczaj ma wiele linijek.\n\nZ poważaniem,\nNazwisko Imię",
                listOf(Recipient("0", "Kowalski Jan", 0, 0, 2, "hash"))
        ).blockingGet()

        server.takeRequest()

        val parser = JsonParser()
        val expected = parser.parse(MessagesTest::class.java.getResource("NowaWiadomosc.json").readText())
        val request = server.takeRequest()
        val actual = parser.parse(request.body.readUtf8())

        assertEquals(expected, actual)
        assertEquals(
            "lX9xvk-OBA0VmHrNIFcQp2xVBZhza9tJ1QbYVKXGM3lFUr0a-OTDo5xUSQ70ROYKf6ICZ1LSXCfDAURoCmDZ-OEedW8IKtyF1s63HyWKxbmHaP-vsVCsGlN6zRHwx1r4h",
            request.getHeader("X-V-RequestVerificationToken")
        )
        assertEquals("877c4a726ad61667f4e2237f0cf6307a", request.getHeader("X-V-AppGuid"))
        assertEquals("19.02.0001.32324", request.getHeader("X-V-AppVersion"))
    }
}
