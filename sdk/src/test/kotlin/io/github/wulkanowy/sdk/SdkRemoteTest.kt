package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.pojo.Folder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDate.of

@Ignore
@OptIn(ExperimentalCoroutinesApi::class)
class SdkRemoteTest {

    companion object {
        private const val API_KEY = "012345678901234567890123456789AB"
        private const val CERT_KEY = "7EBA57E1DDBA1C249D097A9FF1C9CCDD45351A6A"

        // private const val CERTIFICATE = "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"
        private const val PRIVATE_KEY =
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDCbF5Tt176EpB4cX5U+PZE0XytjJ9ABDZFaBFDkaexbkuNeuLOaARjQEOlUoBmpZQXxAF8HlYqeTvPiTcnSfQIS6EdqpICuQNdwvy6CHFAe2imkbbB0aHPsGep6zH8ZxHbssazkTCnGy0j2ZtGT2/iy1GEvc/p2bOkCVcR1H1GqFp+/XpfaMwi2SRCwc67K8Fu8TjSDKNvcRT9nuenGoPA1CWoOiOCxhQA6gnB8LULPel6TVDxeBVdYor/z2GxFe/m0pa7XAKzveuUDhH8k8NlNG65MjvZhgy9iFs+eBUq7lCZ0nuIsDzjnUrLSl4ciYKj9d94qrUyF8L8D9Rl+0WlAgMBAAECggEAQ6jg3rNmyxIg0rl0ZG/LjEF26RKR7P5KQLcpouESga3HfzHvsjMCq+OWZvciFhazRd4BQkdwZxGPnfa7ieGzmhtvs1pDu8zU/hE4UClV+EG6NpVpC2Q/sn5KZRijaZoY3eMGQUFatBzCBcLZxYspfbyR3ucLbu9DE+foNB1Fh4u9RCDj3bClTsqPcNBIaLMpYr3f/bM1fFbS9LrJ7AXZQtGg/2MH58WsvV67SiYAQqGCzld/Jp74gmod4Ii0w2XWZ7OeixdF2xr1j7TK0dUUlrrOrb1cgOWSOEXyy3RX/iF7R8uuLXiRfo1URh6VNPoOtrC6fHCrCp1iRBo08qOk4QKBgQDxqLrWA7gKcTr2yQeGfETXOAYi0xqbNj5A9eVC0XngxnFuwWc5zyg3Ps3c0UK2qTSSFv4SoeEHQM+U0+9LjYzIRSUH7zy4zBrBlLtTQCysSuuZ9QfgO55b3/QEYkyx6Hz/z/gg53jKHjsUKIftGMwJ6C1M2svbBNYCsWrUuYcsbQKBgQDN9gkVDABIeWUtKDHyB5HGcVbsg7Ji7GhMjdFA0GB+9kR0doKNctrzxKn65BI2uTWg+mxaw5V+UeJOIaeFsv2uClYJYn1F55VT7NIx3CLFv6zFRSiMSKz2W+NkwGjQqR7D3DeEyalpjeQeMdpHZg27LMbdVkzy/cK8EM9ZQlRLGQKBgQCpB2wn5dIE+85Sb6pj1ugP4Y/pK9+gUQCaT2RcqEingCY3Ye/h75QhkDxOB9CyEwhCZvKv9aqAeES5xMPMBOZD7plIQ34lhB3y6SVdxbV5ja3dshYgMZNCkBMOPfOHPSaxh7X2zfEe7qZEI1Vv8bhF9bA54ZBVUbyfhZlD0cFKwQKBgQC9BnXHb0BDQ8br7twH+ZJ8wkC4yRXLXJVMzUujZJtrarHhAXNIRoVU/MXUkcV1m/3wRGV119M4IAbHFnQdbO0N8kaMTmwS4DxYzh0LzbHMM+JpGtPgDENRx3unWD/aYZzuvQnnQP3O9n7Kh46BwNQRWUMamL3+tY8n83WZwhqC4QKBgBTUzHy0sEEZ3hYgwU2ygbzC0vPladw2KqtKy+0LdHtx5pqE4/pvhVMpRRTNBDiAvb5lZmMB/B3CzoiMQOwczuus8Xsx7bEci28DzQ+g2zt0/bC2Xl+992Ass5PP5NtOrP/9QiTNgoFSCrVnZnNzQqpjCrFsjfOD2fiuFLCD6zi6"
    }

    @Test
    fun getStudents_api() {
        val sdk = Sdk()
        sdk.setSimpleHttpLogger { println(it) }
        val students = runBlocking { sdk.getStudentsFromMobileApi(token = "FK100000", pin = "999999", symbol = "powiatwulkanowy", apiKey = API_KEY, firebaseToken = "") }
        assertEquals(2, students.size)
    }

    @Test
    fun getStudents_scrapper() {
        val sdk = Sdk().apply {
            // mode = Sdk.Mode.SCRAPPER
        }

        val students =
            runBlocking { sdk.getStudentsFromScrapper(email = "jan@fakelog.cf", password = "jan123", scrapperBaseUrl = "http://fakelog.cf", symbol = "powiatwulkanowy") }
        assertEquals(6, students.size)
    }

    @Test
    fun getStudents_hybrid() {
        val sdk = Sdk().apply {
            // mode = Sdk.Mode.HYBRID
        }

        val students = runBlocking {
            sdk.getStudentsHybrid(
                email = "jan@fakelog.cf",
                password = "jan123",
                apiKey = API_KEY,
                scrapperBaseUrl = "http://fakelog.cf",
                startSymbol = "powiatwulkanowy",
                firebaseToken = ""
            )
        }
        assertEquals(6, students.size)
    }

    @Test
    fun getSemesters_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 1
            classId = 14
        }

        val semesters = runBlocking { sdk.getSemesters() }
        assertEquals(2, semesters.size)
    }

    @Test
    fun getGrades_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val grades = runBlocking { sdk.getGradesDetails(1) }
        assertEquals(22, grades.size)
    }

    @Test
    fun getGrades_scrapper() = runTest {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY

            mode = Sdk.Mode.SCRAPPER
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"

            diaryId = 15
            schoolYear = 2019
            studentId = 1

            loginType = Sdk.ScrapperLoginType.STANDARD
            scrapperBaseUrl = "http://fakelog.cf"
            email = "jan@fakelog.cf"
            password = "jan123"
        }

        val grades = sdk.getGradesDetails(1)
        val grades2 = sdk.getGradesFull(1)
        assertEquals(22, grades.size)
        assertEquals(22, grades2.details.size)
    }

    @Test
    fun getGradesSummary_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val grades = runBlocking { sdk.getGradesSummary(1) }
        assertEquals(4, grades.size)
    }

    @Test
    fun getAttendance_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val attendance = runBlocking { sdk.getAttendance(of(2018, 1, 1), of(2018, 1, 2), 1) }
        assertEquals(24, attendance.size)
    }

    @Test
    fun getSubjects_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val subjects = runBlocking { sdk.getSubjects() }
        assertEquals(14, subjects.size)
    }

    @Test
    fun getNotes_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val notes = runBlocking { sdk.getNotes(1) }
        assertEquals(5, notes.size)
    }

    @Test
    fun getTeachers_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val teachers = runBlocking { sdk.getTeachers(1) }
        assertEquals(9, teachers.size)
    }

    @Test
    fun getHomework_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val homework = runBlocking { sdk.getHomework(of(2018, 1, 1), of(2018, 1, 2)) }
        assertEquals(4, homework.size)
    }

    @Test
    fun getTimetable_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val (timetable) = runBlocking { sdk.getTimetable(of(2018, 1, 1), of(2018, 1, 2)) }
        assertEquals(24, timetable.size)
    }

    @Test
    fun getMessages_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            studentId = 15
            classId = 14
        }

        val messages = runBlocking { sdk.getMessages(Folder.RECEIVED) }
        assertEquals(2, messages.size)

        val messagesSent = runBlocking { sdk.getMessages(Folder.SENT) }
        assertEquals(1, messagesSent.size)

        val messagesTrashed = runBlocking { sdk.getMessages(Folder.TRASHED) }
        assertEquals(1, messagesTrashed.size)
    }

    @Test
    fun readMessage_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            loginId = 16
            studentId = 15
            classId = 14
        }

        val message = runBlocking { sdk.getMessageDetails("asdf") }
        assertEquals("Zmiana statusu wiadomości.", message.content)
    }

    @Test
    fun deleteMessage_api() {
        val sdk = Sdk().apply {
            privateKey = PRIVATE_KEY
            certKey = CERT_KEY

            mobileBaseUrl = "https://api.fakelog.cf/powiatwulkanowy"
            mode = Sdk.Mode.API
            symbol = "powiatwulkanowy"

            schoolSymbol = "123456"
            loginId = 16
            studentId = 15
            classId = 14
        }

        val isDeleted = runBlocking { sdk.deleteMessages(listOf()) }
    }
}
