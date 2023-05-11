package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import io.github.wulkanowy.sdk.scrapper.messages.Folder
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDate
import java.time.Month

@OptIn(ExperimentalCoroutinesApi::class)
@Ignore
class ScrapperRemoteTest : BaseTest() {

    private var api = Scrapper()

    @Before
    fun setUp() {
        api.apply {
            logLevel = HttpLoggingInterceptor.Level.BASIC
            loginType = Scrapper.LoginType.STANDARD
            ssl = true
            host = "fakelog.cf"
            symbol = "powiatwulkanowy"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolId = "123456"
            studentId = 1
            diaryId = 101
            kindergartenDiaryId = 1
            classId = 1
            androidVersion = "9.0"
            buildTag = "Wulkanowy"
            addInterceptor(
                interceptor = {
                    println("Request event ${it.request().url.host}")
                    it.proceed(it.request())
                },
                network = true,
            )
        }
    }

    @Test
    fun getPasswordResetCaptchaCode() {
        val code = runBlocking { api.getPasswordResetCaptcha("https://fakelog.cf", "Default") }

        assertEquals("https://cufs.fakelog.cf/Default/AccountManage/UnlockAccount", code.first)
        assertEquals("6LeAGMYUAAAAAMszd5VWZTEb5WQHqsNT1F4GCqUd", code.second)
    }

    @Test
    fun sendPasswordResetRequest() {
        val res = runBlocking {
            api.sendPasswordResetRequest(
                registerBaseUrl = "https://fakelog.cf",
                symbol = "Default",
                email = "jan@fakelog.cf",
                captchaCode = "03AOLTBLQRPyr0pWvWLRAgD4hRLfxktoqD2IVweeMuXwbkpR_8S9YQtcS3cAXqUOyEw3NxfvwzV0lTjgFWyl8j3UXGQpsc2nvQcqIofj1N8DYfxvtZO-h24W_S0Z9-fDnfXErd7vERS-Ny4d5IU1FupBAEKvT8rrf3OA3GYYbMM7TwB8b_o9Tt192TqYnSxkyIYE4UdaZnLBA0KIXxlBAoqM6QGlPEsSPK9gmCGx-0hn68w-UBQkv_ghRruf4kpv2Shw5emcP-qHBlv3YjAagsb_358K0v8uGJeyLrx4dXN9Ky02TXFMKYWNHz29fjhfunxT73u_PrsLj56f-MjOXrqO894NkUlJ7RkTTclwIsqXtJ794LEBH--mtsqZBND0miR5-odmZszqiNB3V5UsS5ObsqF_fWMl2TCWyNTTvF4elOGwOEeKiumVpjB6e740COxvxN3vbkNWxP9eeghpd5nPN5l2wUV3VL2R5s44TbqHqkrkNpUOd3h7efs3cQtCfGc-tCXoqLC26LxT7aztvKpjXMuqGEf-7wbQ",
            )
        }

        assertTrue(res.startsWith("Wysłano wiadomość na zapisany w systemie adres e-mail"))
    }

    @Test
    fun testAuthorizePermission() = runTest {
        assertFalse(api.authorizePermission("123456q2934234"))
        assertTrue(api.authorizePermission("72041523721"))
    }

    @Test
    fun studentsTest() = runTest {
        val res = api.getCurrentStudent()
        assertEquals("Jan", res?.studentName)

        val user = api.getUserSubjects()
        val symbol = user.symbols[0]
        val school = symbol.schools[0]
        val student = school.subjects[0] as RegisterStudent

        assertEquals("powiatwulkanowy", symbol.symbol)
        assertEquals("jan@fakelog.cf", user.email)
        assertEquals("Jan", student.studentName)
        assertEquals("Kowalski", student.studentSurname)
        assertEquals("123456", school.schoolId)
        assertEquals(1, student.studentId)
        assertEquals(1, student.classId)
        assertEquals("A", student.className)
        assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", school.schoolName)
    }

    @Test
    fun semestersTest() {
        val semesters = runBlocking { api.getSemesters() }

        semesters[0].run {
            assertEquals(15, diaryId)
            assertEquals("4A", diaryName)
//            assertEquals(true, current)
        }

        semesters[3].run {
            assertEquals(13, diaryId)
            assertEquals("3A", diaryName)
            assertEquals(2017, schoolYear)
        }

        semesters[5].run {
            assertEquals(11, diaryId)
            assertEquals("2A", diaryName)
            assertEquals(2016, schoolYear)
//            assertEquals(11, semesterId)
//            assertEquals(1, semesterNumber)
        }

        semesters[6].run {
            //            assertEquals(12, semesterId)
//            assertEquals(2, semesterNumber)
        }
    }

    @Test
    fun attendanceTest() {
        val attendance = runBlocking { api.getAttendance(getLocalDate(2018, 10, 1)) }

        attendance[0].run {
            assertEquals(1, number)
            assertEquals("Zajęcia z wychowawcą", subject)
            assertEquals(getDate(2018, 10, 1), date)

            assertEquals(AttendanceCategory.PRESENCE, category)
        }

        attendance[1].run {
            assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, category)
        }

        assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, attendance[3].category)
        assertEquals(AttendanceCategory.ABSENCE_UNEXCUSED, attendance[4].category)
        assertEquals(AttendanceCategory.ABSENCE_EXCUSED, attendance[5].category)
        assertEquals(AttendanceCategory.UNEXCUSED_LATENESS, attendance[6].category)
        assertEquals(AttendanceCategory.PRESENCE, attendance[9].category)
    }

    @Test
    fun getSubjects() {
        val subjects = runBlocking { api.getSubjects() }

        assertEquals(17, subjects.size)

        subjects[0].run {
            assertEquals(-1, value)
            assertEquals("Wszystkie", name)
        }
    }

    @Test
    fun attendanceSummaryTest() {
        val attendance = runBlocking { api.getAttendanceSummary() }

        assertEquals(10, attendance.size)

        attendance[0].run {
            assertEquals(Month.SEPTEMBER, month)
            assertEquals(32, presence)
            assertEquals(1, absence)
            assertEquals(2, absenceExcused)
            assertEquals(3, absenceForSchoolReasons)
            assertEquals(4, lateness)
            assertEquals(5, latenessExcused)
            assertEquals(6, exemption)
        }

        assertEquals(64, attendance[1].presence)
    }

    @Test
    fun examsTest() {
        val exams = runBlocking { api.getExams(getLocalDate(2018, 5, 7)) }

        exams[0].run {
            assertEquals(getDate(2018, 5, 7), date)
            assertEquals(getDate(1970, 1, 1), entryDate)
            assertEquals("Matematyka", subject)
            assertEquals("Sprawdzian", typeName)
            assertEquals("Figury na płaszczyźnie.", description)
            assertEquals("Aleksandra Krajewska", teacher)
            assertEquals("AK", teacherSymbol)
        }
    }

    @Test
    fun homeworkTest() {
        val homework = runBlocking { api.getHomework(getLocalDate(2018, 9, 11)) }

        homework[1].run {
            assertEquals(getDate(2018, 9, 11), date)
            assertEquals(getDate(2018, 9, 11), entryDate)
            assertEquals("Etyka", subject)
            assertEquals("Notatka własna do zajęć o ks. Jerzym Popiełuszko.", content)
            assertEquals("Michał Mazur", teacher)
            assertEquals("MM", teacherSymbol)
        }
    }

    @Test
    fun notesTest() {
        val notes = runBlocking { api.getNotes() }

        notes[0].run {
            assertEquals(getDate(2018, 1, 16), date)
            assertEquals("Stanisław Krupa", teacher)
            assertEquals("BS", teacherSymbol)
            assertEquals("Kultura języka", category)
            assertEquals("Litwo! Ojczyzno moja! Ty jesteś jak zdrowie. Ile cię trzeba cenić, ten tylko aż kędy pieprz rośnie gdzie podział się? szukać prawodawstwa.", content)
        }
    }

    @Test
    fun gradesTest() {
        val grades = runBlocking { api.getGrades(865).details }

        // dynamic grade
        grades[7].run {
            assertEquals("Religia", subject)
            assertEquals("1", entry)
            assertEquals("6ECD07", colorHex)
            assertEquals("Kart", symbol)
            assertEquals("", description)
            assertEquals("3,00", weight)
            assertEquals(3.0, weightValue, .0)
            assertEquals(LocalDate.now(), date)
            assertEquals("Michał Mazur", teacher)
        }

        grades[2].run {
            assertEquals("Bież", symbol)
            assertEquals("", description)
        }
    }

    @Test
    fun gradesSummaryTest() {
        val summary = runBlocking { api.getGrades(865).summary }

        summary[2].run {
            assertEquals("Etyka", name)
            assertEquals("4", predicted)
            assertEquals("3", final)
        }

        summary[5].run {
            assertEquals("Historia", name)
            assertEquals("4", predicted)
            assertEquals("2+", final)
        }

        summary[8].run {
            assertEquals("Język niemiecki", name)
            assertEquals("", predicted)
            assertEquals("", final)
        }
    }

    @Test
    fun gradesStatisticsTest() {
        val stats = runBlocking { api.getGradesPartialStatistics(321) }

        assertEquals("Język polski", stats[0].subject)
        assertEquals("Matematyka", stats[1].subject)

        val annual = runBlocking { api.getGradesSemesterStatistics(123) }

        assertEquals("Język angielski", annual[0].subject)
    }

    @Test
    fun teachersTest() {
        val teachers = runBlocking { api.getTeachers() }

        assertEquals("Historia", teachers[1].subject)
        assertEquals("Aleksandra Krajewska", teachers[1].name)
        assertEquals("AK", teachers[1].short)
    }

    @Test
    fun schoolTest() {
        val school = runBlocking { api.getSchool() }

        assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", school.name)
    }

    @Test
    fun studentInfoTest() {
//         val info = runBlocking { api.getStudentInfo() }
//
//         info.run {
//             assertEquals("Jan Marek Kowalski", student.fullName)
//             assertEquals("Jan", student.firstName)
//             assertEquals("Marek", student.secondName)
//             assertEquals("Kowalski", student.surname)
//             assertEquals(getDate(1970, 1, 1), student.birthDate)
//             assertEquals("Warszawa", student.birthPlace)
//             assertEquals("12345678900", student.pesel)
//             assertEquals("Mężczyzna", student.gender)
//             assertEquals("1", student.polishCitizenship)
//             assertEquals("Nowak", student.familyName)
//             assertEquals("Monika, Kamil", student.parentsNames)
//
//             assertEquals("", student.address)
//             assertEquals("", student.registeredAddress)
//             assertEquals("", student.correspondenceAddress)
//
//             assertEquals("", student.phoneNumber)
//             assertEquals("-", student.cellPhoneNumber)
// //            assertEquals("jan@fakelog.cf", student.email)
//
//             family[0].run {
//                 assertEquals("Monika Nowak", fullName)
//                 assertEquals("-", email)
//             }
//
//             assertEquals("-", family[1].email)
//         }
    }

    @Test
    fun messagesTest() {
        val mailboxes = runBlocking { api.getMailboxes() }
        assertEquals(2, mailboxes.size)

        val recipients = runBlocking { api.getRecipients("asdf") }
        assertEquals(10, recipients.size)

        val messages = runBlocking { api.getMessages(Folder.RECEIVED) }
        assertEquals(19, messages.size)

        val inbox = runBlocking { api.getReceivedMessages() }
        assertEquals(19, inbox.size)

        val sent = runBlocking { api.getSentMessages() }
        assertEquals(1, sent.size)

        val trash = runBlocking { api.getDeletedMessages() }
        assertEquals(8, trash.size)

        val replayDetails = runBlocking { api.getMessageReplayDetails("uuidv4") }
        assertEquals(1, replayDetails.recipients.size)

        val details = runBlocking { api.getMessageDetails("asdf", true) }
        assertEquals(27214, details.id)
    }

    @Test
    fun sendMessage() {
        runBlocking {
            api.sendMessage(
                subject = "Temat wiadomości",
                content = "Treść",
                recipients = listOf("asdf"),
                senderMailboxId = "uuidv4",
            )
        }
    }

    @Test
    fun devicesTest() {
        val devices = runBlocking { api.getRegisteredDevices() }

        assertEquals(2, devices.size)
    }

    @Test
    fun tokenTest() {
        val tokenizer = runBlocking { api.getToken() }

        tokenizer.run {
            assertEquals("FK100000", token)
            assertEquals("powiatwulkanowy", symbol)
            assertEquals("999999", pin)
        }
    }

    @Test
    fun unregisterTest() {
        val unregister = runBlocking { api.unregisterDevice(1234) }

        assertEquals(true, unregister)
    }

    @Test
    fun timetableTest() {
        val timetable = runBlocking { api.getTimetable(getLocalDate(2018, 9, 17)) }

        timetable.lessons[0].run {
            assertEquals(1, number)
            assertEquals("Fizyka", subject)
            assertEquals("Karolina Kowalska", teacher)
            assertEquals("", group)
            assertEquals("213", room)
            assertEquals("", info)
            assertEquals(false, canceled)
            assertEquals(false, changes)
        }
    }

    @Test
    fun realizedTest() {
        val realized = runBlocking { api.getCompletedLessons(getLocalDate(2018, 9, 17)) }

        realized[0].run {
            assertEquals(getDate(2018, 9, 17), date)
            assertEquals(1, number)
            assertEquals("Historia i społeczeństwo", subject)
            assertEquals("Powstanie listopadowe", topic)
            assertEquals("Histeryczna Jadwiga", teacher)
            assertEquals("Hi", teacherSymbol)
            assertEquals("Nieobecność nieusprawiedliwiona", absence)
        }
    }

    @Test
    fun luckyNumberTest() {
        val luckyNumber = runBlocking { api.getKidsLuckyNumbers() }

        assertEquals(25, luckyNumber[0].number)
    }

    @Test
    fun freeDays() {
        val freeDays = runBlocking { api.getFreeDays() }
        assertEquals(2, freeDays.size)
    }
}
