package io.github.wulkanowy.sdk.scrapper.messages

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import io.github.wulkanowy.sdk.scrapper.repository.MessagesRepository
import io.github.wulkanowy.sdk.scrapper.service.MessagesService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class MessagesTest : BaseLocalTest() {

    private val api by lazy {
        MessagesRepository(
            api = getService(MessagesService::class.java, "http://fakelog.localhost:3000/", false),
        )
    }

    @Test
    fun getRecipients() = runTest {
        with(server) {
            enqueue("Adresaci.json")
            start(3000)
        }

        val recipients = api.getRecipients("uuidv4")

        assertEquals(4, recipients.size)

        recipients[0].run {
            assertEquals("d09e482b-692e-41ff-96e4-b0b4647d0c80", mailboxGlobalKey)
            assertEquals("Tracz Janusz", userName)
            assertEquals("Fake123456", schoolNameShort)
            assertEquals(RecipientType.EMPLOYEE, type)
        }

        recipients[3].run {
            assertEquals("33eae6c8-4dc6-42dd-a4f3-c54d324aa38f", mailboxGlobalKey)
            assertEquals("Kowalski Jan", userName)
            assertEquals("Fake123456", schoolNameShort)
            assertEquals(RecipientType.EMPLOYEE, type)
        }
    }

    @Test
    fun getReceivedMessagesTest() = runTest {
        with(server) {
            enqueue("Odebrane.json")
            start(3000)
        }

        val messages = api.getReceivedMessages("", 0, 50)

        assertEquals(4, messages.size)
        with(messages[0]) {
            assertTrue(isRead)
            assertEquals("Temat wiadomości", subject)
            assertEquals("5be515a7-ded9-4e4d-bb29-15cc254e341f", apiGlobalKey)
            assertEquals(getLocalDateTime(2022, 8, 15, 14, 17, 11), date)
            assertEquals("Jan Sierpień - P - (123456)", correspondents)
            assertEquals("Jan Kowalski - U - (123456)", mailbox)
            assertEquals(2, userRole)
            assertFalse(isMarked)
            assertFalse(isAttachments)
            assertEquals(35232, id)
        }
        assertEquals(true, messages[1].isAttachments)
    }

    @Test
    fun getDeletedMessagesTest() = runTest {
        with(server) {
            enqueue("Usuniete.json")
            start(3000)
        }

        val messages = api.getDeletedMessages(null)

        assertEquals(1, messages.size)
        assertEquals("Nazwisko Imię - P - (000012)", messages[0].correspondents)
        assertEquals("Kowalski Jan - U - (000012)", messages[0].mailbox)
    }

    @Test
    fun getMessagesSentTest() = runTest {
        with(server) {
            enqueue("Wyslane.json")
            start(3000)
        }

        val messages = api.getSentMessages(null)

        assertEquals(2, messages.size)

        messages[0].run {
            assertEquals(32798, id)
            assertEquals("Usprawiedliwienie nieobecności", subject)
            assertEquals("Jan Kowalski - P - (123456)", correspondents)
            assertEquals("0/1", readUnreadBy)
        }

        messages[1].run {
            assertEquals("1/0", readUnreadBy)
        }
    }

    @Test
    fun getMessageReplayDetailsTest() = runTest {
        with(server) {
            enqueue("WiadomoscOdpowiedzPrzekaz.json")
            start(3000)
        }

        val res = api.getMessageReplayDetails("uuidv4")

        with(res) {
            assertEquals(35232, id)
            assertEquals("Jan Sierpień - P - (123456)", sender.fullName)
            assertEquals("20bd8141-6ff0-474c-8aaf-284e6fbdf9c5", sender.mailboxGlobalKey)
            assertEquals("4f947f6c-d001-48ae-ba7a-f150f9f2dcf4", mailboxId)
            assertEquals("Jan Kowalski - U - (123456)", recipients[0].fullName)
            assertEquals(125, res.content.length)
        }
    }

    @Test
    fun sendMessageTest() = runBlocking {
        with(server) {
            enqueue("WiadomoscNowa.json")
            start(3000)
        }

        mockkStatic(UUID::randomUUID)
        every { UUID.randomUUID() } returns mockk {
            every { this@mockk.toString() } returnsMany listOf(
                "1dc8c7a9-983f-4ea7-a4f1-d918beeed389",
                "bfa544fb-0588-4aa0-afd0-1b9db31ce3ea",
            )
        }

        api.sendMessage(
            subject = "Temat wiadomości",
            content = "Tak wygląda zawartość wiadomości.\nZazwyczaj ma wiele linijek.\n\nZ poważaniem,\nNazwisko Imię",
            senderMailboxId = "05e5be82-b894-4864-823b-e79e4f91ce2d",
            recipients = listOf("41c81103-a648-42b1-8519-ae3b2db6ea9b"),
        )

        val expected = Json.decodeFromString<SendMessageRequest>(MessagesTest::class.java.getResource("WiadomoscNowa.json")!!.readText())
        val request = server.takeRequest()
        val actual = Json.decodeFromString<SendMessageRequest>(request.body.readUtf8())

        assertEquals(expected, actual)
        // todo
        // assertEquals(
        //     "lX9xvk-OBA0VmHrNIFcQp2xVBZhza9tJ1QbYVKXGM3lFUr0a-OTDo5xUSQ70ROYKf6ICZ1LSXCfDAURoCmDZ-OEedW8IKtyF1s63HyWKxbmHaP-vsVCsGlN6zRHwx1r4h",
        //     request.getHeader("X-V-RequestVerificationToken"),
        // )
        // assertEquals("877c4a726ad61667f4e2237f0cf6307a", request.getHeader("X-V-AppGuid"))
        // assertEquals("19.02.0001.32324", request.getHeader("X-V-AppVersion"))
    }

    @Test
    fun deleteMessageTest() = runBlocking {
        with(server) {
            enqueue(MockResponse()) // 204
            start(3000)
        }

        val messagesIds = listOf(
            "72b8ce21-3e4c-4f82-9a0e-aaf1783c2317",
            "acc98ff1-d91a-4151-a863-2a97827f7849",
        )
        api.deleteMessages(messagesIds, false)

        val expected = Json.decodeFromString<List<String>>(MessagesTest::class.java.getResource("MoveTrash.json")!!.readText())
        val request = server.takeRequest()
        val actual = Json.decodeFromString<List<String>>(request.body.readUtf8())

        assertEquals(expected, actual)
        // todo
        // assertEquals(
        //     "lX9xvk-OBA0VmHrNIFcQp2xVBZhza9tJ1QbYVKXGM3lFUr0a-OTDo5xUSQ70ROYKf6ICZ1LSXCfDAURoCmDZ-OEedW8IKtyF1s63HyWKxbmHaP-vsVCsGlN6zRHwx1r4h",
        //     request.getHeader("X-V-RequestVerificationToken"),
        // )
        // assertEquals("877c4a726ad61667f4e2237f0cf6307a", request.getHeader("X-V-AppGuid"))
        // assertEquals("19.02.0001.32324", request.getHeader("X-V-AppVersion"))
    }
}
