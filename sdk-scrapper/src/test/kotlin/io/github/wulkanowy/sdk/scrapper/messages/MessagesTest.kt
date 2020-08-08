package io.github.wulkanowy.sdk.scrapper.messages

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.ScrapperException
import io.github.wulkanowy.sdk.scrapper.login.LoginTest
import io.github.wulkanowy.sdk.scrapper.repository.MessagesRepository
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessagesTest : BaseLocalTest() {

    private val api by lazy {
        MessagesRepository(getService(MessagesService::class.java, "http://fakelog.localhost:3000/", false))
    }

    @Test
    fun getRecipients() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        val recipients = runBlocking { api.getRecipients(6) }

        assertEquals(4, recipients.size)

        recipients[0].run {
            assertEquals("18rPracownik", id)
            assertEquals("Tracz Janusz [TJ] - pracownik (Fake123456)", name)
            assertEquals("Tracz Janusz", shortName)
            assertEquals(18, loginId)
            assertEquals(6, reportingUnitId)
            assertEquals(2, role)
            assertEquals("NTVhNTQwMDhhZDFiYTU4OWFhMjEwZDI2MjljMWRmNDE=", hash)
        }

        recipients[3].run {
            assertEquals("96rPracownik", id)
            assertEquals("Kowalski Jan (JK) - pracownik [Fake123456]", name)
            assertEquals("Kowalski Jan", shortName)
            assertEquals(96, loginId)
            assertEquals(6, reportingUnitId)
            assertEquals(2, role)
            assertEquals("NTVhNTQwMDhhZDFiYTU4OWFhMjEwZDI2MjljMWRmNDE=", hash)
        }
    }

    @Test
    fun getReceivedMessagesTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciOdebrane.json").readText()))
        server.start(3000)

        val messages = runBlocking { api.getReceivedMessages(null, null) }

        assertEquals(2, messages.size)
        with(messages[0]) {
            assertEquals(false, unread)
            assertEquals(null, content)
            assertEquals("Temat wiadomości", subject)
            assertEquals("Nazwisko Imię", sender)
            assertEquals(27214, messageId)
            assertEquals(3617, senderId)
            assertEquals(true, hasAttachments)
            assertEquals(35232, id)
        }
        assertEquals(false, messages[1].hasAttachments)
    }

    @Test
    fun getDeletedMessagesTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciUsuniete.json").readText()))
        server.start(3000)

        assertEquals(1, runBlocking { api.getDeletedMessages(null, null) }.size)
    }

    @Test
    fun getMessagesSentTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.start(3000)

        val messages = runBlocking { api.getSentMessages(null, null) }

        assertEquals(6, messages.size)

        messages[0].run {
            assertEquals(32798, id)
            assertEquals(32798, messageId)
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

        runBlocking { api.getSentMessages(null, null) }[1].run {
            assertEquals("Czerwieńska - Kowalska Joanna", recipient)
        }
    }

    @Test
    fun getMessagesSent_multiRecipients() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.start(3000)

        runBlocking { api.getSentMessages(null, null) }[3].run {
            assertEquals("Czerwieńska - Kowalska Joanna; Tracz Janusz", recipient)
        }
    }

    @Test
    fun getMessagesSent_multiRecipientsWithBraces() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WiadomosciWyslane.json").readText()))
        server.start(3000)

        runBlocking { api.getSentMessages(null, null) }[5].run {
            assertEquals("Tracz Antoni; Kowalska Joanna", recipient)
        }
    }

    @Test
    fun getMessageRecipientsTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        runBlocking { api.getMessageRecipients(421, 0) }[0].run {
            assertEquals("18rPracownik", id)
            assertEquals("Tracz Janusz [TJ] - pracownik (Fake123456)", name)
            assertEquals(18, loginId)
//            assertEquals(null, reportingUnitId)
            assertEquals(2, role)
            assertEquals("NTVhNTQwMDhhZDFiYTU4OWFhMjEwZDI2MjljMWRmNDE=", hash)
            assertEquals("Tracz Janusz", shortName)
        }
    }

    @Test
    fun getMessageSenderTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Adresaci.json").readText()))
        server.start(3000)

        runBlocking { api.getMessageRecipients(421, 94) }[1].run {
            assertEquals("94rPracownik", id)
            assertEquals(94, loginId)
        }
    }

    @Test
    fun getMessageContentTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Wiadomosc.json").readText()))
        server.start(3000)

        assertEquals(90, runBlocking { api.getMessage(1, 1, false, 0) }.length)
    }

    @Test
    fun getMessageAttachmentsTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Wiadomosc.json").readText()))
        server.start(3000)

        val attachments = runBlocking { api.getMessageAttachments(1, 1) }

        assertEquals(1, attachments.size)
        with(attachments[0]) {
            assertEquals(131, id)
            assertEquals("nazwa_pliku.pptx", filename)
            assertEquals(35232, messageId)
            assertEquals("0123456789ABCDEF!123", oneDriveId)
            assertEquals("https://1drv.ms/u/s!AmvjLDq5anT2psJ4nujoBUyclWOUhw", url)
        }
    }

    @Test
    fun sendMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Start.html").readText()))
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("WyslanaWiadomosc.json").readText()))
        server.start(3000)

        runBlocking {
            api.sendMessage(
                "Temat wiadomości", "Tak wygląda zawartość wiadomości.\nZazwyczaj ma wiele linijek.\n\nZ poważaniem,\nNazwisko Imię",
                listOf(Recipient("0", "Kowalski Jan", 0, 0, 2, "hash"))
            )
        }

        server.takeRequest()

        val expected = jsonParser.parse(MessagesTest::class.java.getResource("NowaWiadomosc.json").readText())
        val request = server.takeRequest()
        val actual = jsonParser.parse(request.body.readUtf8())

        assertEquals(expected, actual)
        assertEquals(
            "lX9xvk-OBA0VmHrNIFcQp2xVBZhza9tJ1QbYVKXGM3lFUr0a-OTDo5xUSQ70ROYKf6ICZ1LSXCfDAURoCmDZ-OEedW8IKtyF1s63HyWKxbmHaP-vsVCsGlN6zRHwx1r4h",
            request.getHeader("X-V-RequestVerificationToken")
        )
        assertEquals("877c4a726ad61667f4e2237f0cf6307a", request.getHeader("X-V-AppGuid"))
        assertEquals("19.02.0001.32324", request.getHeader("X-V-AppVersion"))
    }

    @Test
    fun sendMessage_error() {
        server.enqueue("ADFSLight-form-resman.html", LoginTest::class.java)
        server.start(3000)

        val api = MessagesRepository(getService(MessagesService::class.java, "http://fakelog.localhost:3000/", false, getOkHttp(loginType = Scrapper.LoginType.ADFSLight)))
        try {
            runBlocking { api.sendMessage("Temat", "Treść", listOf()) }
        } catch (e: Throwable) {
            assertTrue(e is ScrapperException)
            assertEquals("User not logged in", e.message)
        }
    }

    @Test
    fun deleteMessageTest() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Start.html").readText()))
        server.enqueue(MockResponse().setBody("{\"success\": true}"))
        server.start(3000)

        assertEquals(runBlocking { api.deleteMessages(listOf(Pair(74, 1), Pair(69, 2))) }, true)

        server.takeRequest()

        val expected = jsonParser.parse(MessagesTest::class.java.getResource("UsunWiadomosc.json").readText())
        val request = server.takeRequest()
        val actual = jsonParser.parse(request.body.readUtf8())

        assertEquals(expected, actual)
        assertEquals(
            "lX9xvk-OBA0VmHrNIFcQp2xVBZhza9tJ1QbYVKXGM3lFUr0a-OTDo5xUSQ70ROYKf6ICZ1LSXCfDAURoCmDZ-OEedW8IKtyF1s63HyWKxbmHaP-vsVCsGlN6zRHwx1r4h",
            request.getHeader("X-V-RequestVerificationToken")
        )
        assertEquals("877c4a726ad61667f4e2237f0cf6307a", request.getHeader("X-V-AppGuid"))
        assertEquals("19.02.0001.32324", request.getHeader("X-V-AppVersion"))
    }

    @Test
    fun deleteMessage_emptyResponse() {
        server.enqueue(MockResponse().setBody(MessagesTest::class.java.getResource("Start.html").readText()))
        server.enqueue(MockResponse().setBody(""))
        server.start(3000)

        try {
            runBlocking { api.deleteMessages(listOf(Pair(74, 1), Pair(69, 2))) }
        } catch (e: Throwable) {
            assertEquals("Unexpected empty response. Message(s) may already be deleted", e.message)
        }
    }
}
