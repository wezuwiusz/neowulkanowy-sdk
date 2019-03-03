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
    fun getRecipients() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        val recipients = api.getRecipients(6).blockingGet()

        assertEquals(3, recipients.size)

        recipients[0].run {
            assertEquals("18rPracownik", id)
            assertEquals("Tracz Janusz [TJ] - pracownik (Fake123456)", name)
            assertEquals("Tracz Janusz", shortName)
            assertEquals(18, loginId)
            assertEquals(6, reportingUnitId)
            assertEquals(2, role)
            assertEquals("NTVhNTQwMDhhZDFiYTU4OWFhMjEwZDI2MjljMWRmNDE=", hash)
        }
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
        server.start(3000)

        val messages = api.getSentMessages(null, null).blockingGet()

        assertEquals(6, messages.size)

        messages[0].run {
            assertEquals(32798, id)
            assertEquals(32798, messageId)
            assertEquals(getDate(2018, 6, 11, 9, 38, 35), date)
            assertEquals("Usprawiedliwienie nieobecności", subject)
            assertEquals("Tracz Janusz", recipient)
            assertEquals(1, unreadBy)
            assertEquals(0, readBy)
        }
    }

    @Test
    fun getMessagesSent_recipientWithDashInName() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[1].run {
            assertEquals("Czerwieńska - Kowalska Joanna", recipient)
        }
    }

    @Test
    fun getMessagesSent_multiRecipients() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[3].run {
            assertEquals("Czerwieńska - Kowalska Joanna; Tracz Janusz", recipient)
        }
    }

    @Test
    fun getMessagesSent_multiRecipientsWithBraces() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.start(3000)

        api.getSentMessages(null, null).blockingGet()[5].run {
            assertEquals("Tracz Antoni (TA); Kowalska Joanna", recipient)
        }
    }

    @Test
    fun getMessageRecipientsTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        api.getMessageRecipients(421).blockingGet()[0].run {
            assertEquals("18rPracownik", id)
            assertEquals(18, loginId)
        }
    }

    @Test
    fun getMessageContentTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Wiadomosc.json").readText()))
        server.start(3000)

        assertEquals(90, api.getMessage(1, 1, false, 0).blockingGet().length)
    }

    @Test
    fun sendMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Start.html").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WyslanaWiadomosc.json").readText()))
        server.start(3000)

        api.sendMessage(
            "Temat wiadomości", "Tak wygląda zawartość wiadomości.\nZazwyczaj ma wiele linijek.\n\nZ poważaniem,\nNazwisko Imię",
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
