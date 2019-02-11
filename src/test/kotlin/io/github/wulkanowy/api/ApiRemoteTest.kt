package io.github.wulkanowy.api

import io.github.wulkanowy.api.attendance.Attendance
import io.github.wulkanowy.api.attendance.AttendanceSummary
import io.github.wulkanowy.api.attendance.Subject
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.grades.Grade
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.api.grades.GradeSummary
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.messages.Folder
import io.github.wulkanowy.api.messages.Message
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.notes.Note
import io.github.wulkanowy.api.realized.Realized
import io.github.wulkanowy.api.register.Semester
import io.github.wulkanowy.api.register.Student
import io.github.wulkanowy.api.school.School
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.student.StudentInfo
import io.github.wulkanowy.api.timetable.Timetable
import io.reactivex.observers.TestObserver
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.threeten.bp.Month

@Ignore
class ApiRemoteTest : BaseTest() {

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
            useNewStudent = true
            setInterceptor(Interceptor {
                println("Request event ${it.request().url().host()}")
                it.proceed(it.request())
            }, true, 0)
        }
    }

    @Test
    fun studentsTest() {
        val students = api.getStudents()
        val studentObserver = TestObserver<List<Student>>()
        students.subscribe(studentObserver)
        studentObserver.assertComplete()

        val values = studentObserver.values()[0]

        values[0].run {
            assertEquals("Default", symbol)
            assertEquals("jan@fakelog.cf", email)
            assertEquals("Jan Kowalski", studentName)
            assertEquals("123456", schoolSymbol)
            assertEquals(1, studentId)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
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
            assertEquals(15, diaryId)
            assertEquals("4A 2018", diaryName)
            assertEquals(true, current)
        }

        values[2].run {
            assertEquals(13, diaryId)
            assertEquals("3A 2017", diaryName)
        }

        values[4].run {
            assertEquals(11, diaryId)
            assertEquals("2A 2016", diaryName)
//            assertEquals(11, semesterId)
            assertEquals(1, semesterNumber)
        }

        values[5].run {
//            assertEquals(12, semesterId)
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
            assertEquals("Zajęcia z wychowawcą", subject)
            assertEquals(getDate(2018, 10, 1), date)

            assertEquals("Obecność", name)
            assertTrue(presence)
        }

        values[1].run {
            assertEquals("Nieobecność", name)
            assertTrue(absence)
            assertFalse(excused)
        }

        assertEquals("Nieobecność", values[3].name)
        assertEquals("Nieobecność", values[4].name)
        assertEquals("Nieobecność usprawiedliwiona", values[5].name)
        assertEquals("Spóźnienie", values[6].name)
        assertEquals("Obecność", values[9].name)
    }

    @Test
    fun getSubjects() {
        val subjects = api.getSubjects()
        val subjectsObserver = TestObserver<List<Subject>>()
        subjects.subscribe(subjectsObserver)
        subjectsObserver.assertComplete()

        val values = subjectsObserver.values()[0]

        assertEquals(17, values.size)

        values[0].run {
            assertEquals(-1, value)
            assertEquals("Wszystkie", name)
        }
    }

    @Test
    fun attendanceSummaryTest() {
        val attendance = api.getAttendanceSummary()
        val attendanceObserver = TestObserver<List<AttendanceSummary>>()
        attendance.subscribe(attendanceObserver)
        attendanceObserver.assertComplete()

        val values = attendanceObserver.values()[0]

        assertEquals(10, values.size)

        values[0].run {
            assertEquals(Month.SEPTEMBER, month)
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

        val values = examsObserver.values()[0]

        values[0].run {
            assertEquals(getDate(2018, 5, 7), date)
            assertEquals(getDate(1970, 1, 1), entryDate)
            assertEquals("Matematyka", subject)
            assertEquals("", group)
            assertEquals("Sprawdzian", type)
            assertEquals("Figury na płaszczyźnie.", description)
            assertEquals("Aleksandra Krajewska", teacher)
            assertEquals("AK", teacherSymbol)
        }
    }

    @Test
    fun homeworkTest() {
        val homework = api.getHomework(getLocalDate(2018, 9, 11))
        val homeworkObserver = TestObserver<List<Homework>>()
        homework.subscribe(homeworkObserver)
        homeworkObserver.assertComplete()

        val values = homeworkObserver.values()[0]

        values[1].run {
            assertEquals(getDate(2018, 9, 11), date)
            assertEquals(getDate(2017, 10, 26), entryDate)
            assertEquals("Etyka", subject)
            assertEquals("Notatka własna do zajęć o ks. Jerzym Popiełuszko.", content)
            assertEquals("Michał Mazur", teacher)
            assertEquals("MM", teacherSymbol)
        }
    }

    @Test
    fun notesTest() {
        val notes = api.getNotes()
        val notesObserver = TestObserver<List<Note>>()
        notes.subscribe(notesObserver)
        notesObserver.assertComplete()

        val values = notesObserver.values()[0]

        values[0].run {
            assertEquals(getDate(2018, 1, 16), date)
            assertEquals("Stanisław Krupa", teacher)
            assertEquals("BS", teacherSymbol)
            assertEquals("Kultura języka", category)
            assertEquals("Litwo! Ojczyzno moja! Ty jesteś jak zdrowie. Ile cię trzeba cenić, ten tylko aż kędy pieprz rośnie gdzie podział się? szukać prawodawstwa.", content)
        }
    }

    @Test
    fun gradesTest() {
        val grades = api.getGrades(865)
        val gradesObserver = TestObserver<List<Grade>>()
        grades.subscribe(gradesObserver)
        gradesObserver.assertComplete()

        val values = gradesObserver.values()[0]

        values[5].run {
            assertEquals("Religia", subject)
            assertEquals("1", entry)
            assertEquals("000000", color)
            assertEquals("Kart", symbol)
            assertEquals("", description)
            assertEquals("3,00", weight)
            assertEquals(3, weightValue)
            assertEquals(getDate(2018, 11, 19), date)
            assertEquals("Michał Mazur", teacher)
        }

        values[0].run {
            assertEquals("Bież", symbol)
            assertEquals("", description)
        }
    }

    @Test
    fun gradesSummaryTest() {
        val summary = api.getGradesSummary(865)
        val summaryObserver = TestObserver<List<GradeSummary>>()
        summary.subscribe(summaryObserver)
        summaryObserver.assertComplete()

        val values = summaryObserver.values()[0]

        values[2].run {
            assertEquals("Etyka", name)
            assertEquals("4", predicted)
            assertEquals("4", final)
        }

        values[5].run {
            assertEquals("Historia", name)
            assertEquals("4", predicted)
            assertEquals("4", final)
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
        assertEquals("Aleksandra Krajewska", values[1].name)
        assertEquals("AK", values[1].short)
    }

    @Test
    fun schoolTest() {
        val school = api.getSchool()
        val schoolObserver = TestObserver<School>()
        school.subscribe(schoolObserver)
        schoolObserver.assertComplete()

        val values = schoolObserver.values()[0]

        assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", values.name)
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
//            assertEquals("jan@fakelog.cf", student.email)

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

        val messages = api.getMessages(Folder.RECEIVED)
        val messagesObserver = TestObserver<List<Message>>()
        messages.subscribe(messagesObserver)
        messagesObserver.assertComplete()

        val inbox = api.getReceivedMessages(getLocalDateTime(2015, 10, 5))
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

        val m = api.getMessageContent(del[0].messageId ?: 0, del[0].folderId)
        val mObserver = TestObserver<String>()
        m.subscribe(mObserver)
        mObserver.assertComplete()
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

    @Test
    fun luckyNumberTest() {
        val luckyNumber = api.getLuckyNumber()
        val luckyNumberObserver = TestObserver<Int>()
        luckyNumber.subscribe(luckyNumberObserver)
        luckyNumberObserver.assertComplete()

        assertTrue(luckyNumberObserver.values().size == 0 || luckyNumberObserver.values()[0] == 0)
    }
}
