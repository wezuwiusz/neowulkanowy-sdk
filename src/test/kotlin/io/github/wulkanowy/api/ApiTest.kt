package io.github.wulkanowy.api

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.attendance.AttendanceSummary
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.api.grades.GradeSummary
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.messages.Message
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.realized.Realized
import io.github.wulkanowy.api.register.Pupil
import io.github.wulkanowy.api.register.Semester
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.student.StudentInfo
import io.github.wulkanowy.api.timetable.Timetable
import io.reactivex.observers.TestObserver
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class ApiTest : BaseTest() {

    private var api = Api()

    @Before
    fun setUp() {
        api.apply {
            logLevel = HttpLoggingInterceptor.Level.BASIC
            loginType = Api.LoginType.STANDARD
            ssl = true
            host = "fakelog.cf"
            symbol = "Default"
            email = "jan@fakelog.cf"
            password = "jan123"
            schoolSymbol = "123456"
            studentId = 1
            diaryId = 101
            notifyDataChanged() // unnecessary in this case
            setInterceptor(Interceptor { // important! put bellow notifyDataChanged
                println("Request event ${it.request().url().host()}")
                it.proceed(it.request())
            }, 0)
        }
    }

    @Test
    fun semesterTest() {
        val semester = api.getCurrentSemester()
        val semesterObserver = TestObserver<Semester>()
        semester.subscribe(semesterObserver)
        semesterObserver.assertComplete()

        semesterObserver.values()[0].run {
            assertEquals(101, diaryId)
            assertEquals("1A 2015", diaryName)
            assertEquals(1234568, semesterId)
            assertEquals(2, semesterNumber)
            assertTrue(current)
        }
    }

    @Test
    fun pupilsTest() {
        val pupils = api.getPupils()
        val pupilsObserver = TestObserver<List<Pupil>>()
        pupils.subscribe(pupilsObserver)
        pupilsObserver.assertComplete()

        pupilsObserver.values()[0][0].run {
            assertEquals("Default", symbol)
            assertEquals("jan@fakelog.cf", email)
            assertEquals("Jan Kowalski", studentName)
            assertEquals("123456", schoolSymbol)
            assertEquals(1, studentId)
            assertEquals("Publiczny dziennik Wulkanowego nr 1 w fakelog.cf", schoolName)
        }
    }

    @Test
    fun semestersTest() {
        val semesters = api.getSemesters()
        val semestersObserver = TestObserver<List<Semester>>()
        semesters.subscribe(semestersObserver)
        semestersObserver.assertComplete()

        val values = semestersObserver.values()[0]

        values[1].run {
            assertEquals(101, diaryId)
            assertEquals("1A 2015", diaryName)
            assertEquals(true, current)
        }

        values[2].run {
            assertEquals(202, diaryId)
            assertEquals("II 2016", diaryName)
        }

        values[4].run {
            assertEquals(303, diaryId)
            assertEquals("III 2017", diaryName)
            assertEquals(1234567, semesterId)
            assertEquals(1, semesterNumber)
        }

        values[5].run {
            assertEquals(1234568, semesterId)
            assertEquals(2, semesterNumber)
        }
    }

    @Test
    fun attendanceTest() {
        val attendance = api.getAttendance(getLocalDate(2018, 10, 1))
        val attendanceObserver = TestObserver<List<Attendance>>()
        attendance.subscribe(attendanceObserver)
        attendanceObserver.assertComplete()

        val values = attendanceObserver.values()[0]

        values[0].run {
            assertEquals(1, number)
            assertEquals("Zajęcia artystyczne", subject)
            assertEquals(getDate(2018, 10, 1), date)

            assertEquals("Obecność", name)
            assertTrue(presence)
        }

        values[1].run {
            assertEquals("Nieobecność", name)
            assertTrue(absence)
            assertFalse(excused)
        }

        assertEquals("Spóźnienie", values[3].name)
        assertEquals("Spóźnienie usprawiedliwione", values[4].name)
        assertEquals("Nieobecny z przyczyn szkolnych", values[5].name)
        assertEquals("Zwolniony", values[6].name)
        assertEquals("Obecność", values[9].name)

    }

    @Test
    fun attendanceSummaryTest() {
        val attendance = api.getAttendanceSummary()
        val attendanceObserver = TestObserver<List<AttendanceSummary>>()
        attendance.subscribe(attendanceObserver)
        attendanceObserver.assertComplete()

        val values = attendanceObserver.values()[0]

        assertEquals(12, values.size)

        values[0].run {
            assertEquals("IX", month)
            assertEquals(32, presence)
            assertEquals(1, absence)
            assertEquals(2, absenceExcused)
            assertEquals(3, absenceForSchoolReasons)
            assertEquals(4, lateness)
            assertEquals(5, latenessExcused)
            assertEquals(6, exemption)
        }

        assertEquals(64, values[1].presence)
    }

    @Test
    fun examsTest() {
        val exams = api.getExams(getLocalDate(2018, 5, 7))
        val examsObserver = TestObserver<List<Exam>>()
        exams.subscribe(examsObserver)
        examsObserver.assertComplete()

        examsObserver.values()[0][0].run {
            assertEquals(getDate(2018, 5, 7), date)
            assertEquals(getDate(1970, 1, 1), entryDate)
            assertEquals("Matematyka", subject)
            assertEquals("", group)
            assertEquals("Sprawdzian", type)
            assertEquals("Figury na płaszczyźnie.", description)
            assertEquals("Janusz Tracz", teacher)
            assertEquals("TJ", teacherSymbol)
        }
    }

    @Test
    fun homeworkTest() {
        val homework = api.getHomework(getLocalDate(2017, 10, 23))
        val homeworkObserver = TestObserver<List<Homework>>()
        homework.subscribe(homeworkObserver)
        homeworkObserver.assertComplete()

        homeworkObserver.values()[0][1].run {
            assertEquals(getDate(2017, 10, 23), date)
            assertEquals(getDate(2017, 10, 18), entryDate)
            assertEquals("Metodologia programowania", subject)
            assertEquals("Wszystkie instrukcje warunkowe, pętle (budowa, zasada działania, schemat blokowy)", content)
            assertEquals("Janusz Tracz", teacher)
            assertEquals("TJ", teacherSymbol)
        }
    }

    @Test
    fun notesTest() {
        val notes = api.getNotes()
        val notesObserver = TestObserver<List<Note>>()
        notes.subscribe(notesObserver)
        notesObserver.assertComplete()

        notesObserver.values()[0][0].run {
            assertEquals(getDate(2018, 3, 26), date)
            assertEquals("Janusz Tracz", teacher)
            assertEquals("Udział w konkursie szkolnym +20 pkt", category)
            assertEquals("+ 20p za udział w Konkursie Języka Angielskiego", content)
        }
    }

    @Test
    fun gradesTest() {
        val grades = api.getGrades(864)
        val gradesObserver = TestObserver<List<Grade>>()
        grades.subscribe(gradesObserver)
        gradesObserver.assertComplete()

        val values = gradesObserver.values()[0]

        values[0].run {
            assertEquals("Historia", subject)
            assertEquals("1", entry)
            assertEquals("000000", color)
            assertEquals("Spr", symbol)
            assertEquals("spr.-rozbiory", description)
            assertEquals("5,00", weight)
            assertEquals(5, weightValue)
            assertEquals(getDate(2018, 1, 29), date)
            assertEquals("Janusz Tracz", teacher)
        }

        values[5].run {
            assertEquals("Bież", symbol)
            assertEquals("", description)
        }
    }

    @Test
    fun gradesSummaryTest() {
        val summary = api.getGradesSummary(864)
        val summaryObserver = TestObserver<List<GradeSummary>>()
        summary.subscribe(summaryObserver)
        summaryObserver.assertComplete()

        val values = summaryObserver.values()[0]

        values[2].run {
            assertEquals("Etyka", name)
            assertEquals("2", predicted)
            assertEquals("2", final)
        }

        values[5].run {
            assertEquals("Historia", name)
            assertEquals("1", predicted)
            assertEquals("1", final)
        }

        values[8].run {
            assertEquals("Język niemiecki", name)
            assertEquals("", predicted)
            assertEquals("", final)
        }
    }

    @Test
    fun gradesStatisticsTest() {
        val stats = api.getGradesStatistics(321, false)
        val statsObserver = TestObserver<List<GradeStatistics>>()
        stats.subscribe(statsObserver)

        val values = statsObserver.values()[0]

        assertEquals("Język polski", values[0].subject)
        assertEquals("Matematyka", values[7].subject)

        val annual = api.getGradesStatistics(123, true)
        val annualObserver = TestObserver<List<GradeStatistics>>()
        annual.subscribe(annualObserver)

        val values2 = annualObserver.values()[0]

        assertEquals("Język angielski", values2[0].subject)
    }

    @Test
    fun teachersTest() {
        val teachers = api.getTeachers()
        val teachersObserver = TestObserver<List<Teacher>>()
        teachers.subscribe(teachersObserver)
        teachersObserver.assertComplete()

        val values = teachersObserver.values()[0]

        assertEquals("Historia", values[1].subject)
        assertEquals("Janusz Tracz", values[1].name)
        assertEquals("TJ", values[1].short)
    }

    @Test
    fun studentInfoTest() {
        val info = api.getStudentInfo()
        val studentObserver = TestObserver<StudentInfo>()
        info.subscribe(studentObserver)
        studentObserver.assertComplete()

        studentObserver.values()[0].run {
            assertEquals("Jan Marek Kowalski", student.fullName)
            assertEquals("Jan", student.firstName)
            assertEquals("Marek", student.secondName)
            assertEquals("Kowalski", student.surname)
            assertEquals(getDate(1970, 1, 1), student.birthDate)
            assertEquals("Warszawa", student.birthPlace)
            assertEquals("12345678900", student.pesel)
            assertEquals("Mężczyzna", student.gender)
            assertEquals("1", student.polishCitizenship)
            assertEquals("Nowak", student.familyName)
            assertEquals("Monika, Kamil", student.parentsNames)

            assertEquals("", student.address)
            assertEquals("", student.registeredAddress)
            assertEquals("", student.correspondenceAddress)

            assertEquals("", student.phoneNumber)
            assertEquals("-", student.cellPhoneNumber)
            assertEquals("jan@fakelog.cf", student.email)

            family[0].run {
                assertEquals("Monika Nowak", fullName)
                assertEquals("-", email)
            }

            assertEquals("-", family[1].email)
        }
    }

    @Test
    fun messagesTest() {
        val units = api.getReportingUnits()
        val unitsObserver = TestObserver<List<ReportingUnit>>()
        units.subscribe(unitsObserver)
        unitsObserver.assertComplete()

        val recipients = api.getRecipients()
        val recipientsObserver = TestObserver<List<Recipient>>()
        recipients.subscribe(recipientsObserver)
        recipientsObserver.assertComplete()

        val inbox = api.getReceivedMessages(getLocalDate(2015, 10, 5))
        val inboxObserver = TestObserver<List<Message>>()
        inbox.subscribe(inboxObserver)
        inboxObserver.assertComplete()

        assertEquals(2, inboxObserver.values()[0].size)

        val sent = api.getSentMessages()
        val outObserver = TestObserver<List<Message>>()
        sent.subscribe(outObserver)
        outObserver.assertComplete()

        assertEquals(1, outObserver.values()[0].size)

        val trash = api.getDeletedMessages()
        val trashObserver = TestObserver<List<Message>>()
        trash.subscribe(trashObserver)
        trashObserver.assertComplete()

        val del = trashObserver.values()[0]

        assertEquals(1, del.size)

        val m = api.getMessage(del[0].messageId ?: 0, del[0].folderId)
        val mObserver = TestObserver<Message>()
        m.subscribe(mObserver)
        mObserver.assertComplete()

        assertEquals(1, mObserver.values().size)
    }

    @Test
    fun devicesTest() {
        val devices = api.getRegisteredDevices()
        val devicesObserver = TestObserver<List<Device>>()
        devices.subscribe(devicesObserver)
        devicesObserver.assertComplete()

        val values = devicesObserver.values()[0]

        assertEquals(2, values.size)
    }

    @Test
    fun tokenTest() {
        val tokenizer = api.getToken()
        val tokenObserver = TestObserver<TokenResponse>()
        tokenizer.subscribe(tokenObserver)
        tokenObserver.assertComplete()

        tokenObserver.values()[0].run {
            assertEquals("FK100000", token)
            assertEquals("Default", symbol)
            assertEquals("999999", pin)
        }
    }

    @Test
    fun unregisterTest() {
        val unregister = api.unregisterDevice(1234)
        val unregisterObserver = TestObserver<List<Device>>()
        unregister.subscribe(unregisterObserver)
        unregisterObserver.assertComplete()

        assertEquals(2, unregisterObserver.values()[0].size)
    }

    @Test
    fun timetableTest() {
        val timetable = api.getTimetable(getLocalDate(2018, 9, 17))
        val timetableObserver = TestObserver<List<Timetable>>()
        timetable.subscribe(timetableObserver)
        timetableObserver.assertComplete()

        val values = timetableObserver.values()[0]

        values[0].run {
            assertEquals(1, number)
            assertEquals("Fizyka", subject)
            assertEquals("Janusz Tracz", teacher)
            assertEquals("", group)
            assertEquals("213", room)
            assertEquals("", info)
            assertEquals(false, canceled)
            assertEquals(false, changes)
        }
    }

    @Test
    fun realizedTest() {
        val realized = api.getRealized(getLocalDate(2018, 9, 17))
        val realizedObserver = TestObserver<List<Realized>>()
        realized.subscribe(realizedObserver)
        realizedObserver.assertComplete()

        realizedObserver.values()[0][0].run {
            assertEquals(getDate(2018, 9, 17), date)
            assertEquals(1, number)
            assertEquals("Historia i społeczeństwo", subject)
            assertEquals("Powstanie listopadowe", topic)
            assertEquals("Histeryczna Jadwiga", teacher)
            assertEquals("Hi", teacherSymbol)
            assertEquals("Nieobecność nieusprawiedliwiona", absence)
        }
    }
}
