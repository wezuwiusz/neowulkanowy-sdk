package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.attendance.Attendance
import io.github.wulkanowy.sdk.base.ApiResponse
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.exams.Exam
import io.github.wulkanowy.sdk.grades.Grade
import io.github.wulkanowy.sdk.homework.Homework
import io.github.wulkanowy.sdk.notes.Note
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.github.wulkanowy.sdk.repository.MobileRepository
import io.github.wulkanowy.sdk.repository.RegisterRepository
import io.github.wulkanowy.sdk.timetable.Lesson
import io.reactivex.observers.TestObserver
import junit.framework.TestCase.assertEquals
import org.junit.BeforeClass
import org.junit.Test

const val PASSWORD = "012345678901234567890123456789AB"
const val DEVICE_NAME = "Wulkanowy#client"
const val HOST = "https://api.fakelog.cf"
const val SYMBOL = "Default"
const val TOKEN = "FK100000"
const val PIN = "999999"

class UonetTest {

    companion object {

        private lateinit var mobile: MobileRepository

        private lateinit var student: Student

        @JvmStatic
        @BeforeClass fun setUp() {
            // RegisterRepository
            val register = RegisterRepository(PASSWORD, HOST, SYMBOL)

            val certificate = register.getCertificate(TOKEN, PIN, DEVICE_NAME)
            val certSubscriber = TestObserver<CertificateResponse>()
            certificate.subscribe(certSubscriber)
            certSubscriber.assertComplete()
            certSubscriber.assertNoErrors()

            assertEquals(false, certSubscriber.values()[0].isError)

            val tokenCrt = certSubscriber.values()[0].tokenCert

            register.certKey = tokenCrt!!.certificateKey
            register.certificate = tokenCrt.certificatePfx

            val pupils = register.getPupils()
            val pupilSubscriber = TestObserver<List<Student>>()
            pupils.subscribe(pupilSubscriber)
            pupilSubscriber.assertComplete()
            pupilSubscriber.assertNoErrors()
            assertEquals(2, pupilSubscriber.values()[0].size)

            student = pupilSubscriber.values()[0][0]

            // MobileRepository
            mobile = MobileRepository(PASSWORD, "$HOST/Default", SYMBOL, register.certKey, register.certificate, student.reportingUnitSymbol)
        }
    }

    @Test fun logStartTest() {
        val start = mobile.logStart()
        val startSubscriber = TestObserver<ApiResponse<String>>()
        start.subscribe(startSubscriber)
        startSubscriber.assertComplete()
        startSubscriber.assertNoErrors()
        assertEquals("Ok", startSubscriber.values()[0].status)
    }

    @Test fun dictionariesTest() {
        val dictionaries = mobile.getDictionaries(student.userLoginId, student.classificationPeriodId, student.classId)
        val dictionariesSubscriber = TestObserver<Dictionaries>()
        dictionaries.subscribe(dictionariesSubscriber)
        dictionariesSubscriber.assertComplete()
        dictionariesSubscriber.assertNoErrors()
    }

    @Test fun timetableTest() {
        val lessons = mobile.getTimetable("2018-04-23", "2018-04-24", student.classId, student.classificationPeriodId, student.id)
        val lessonsSubscriber = TestObserver<List<Lesson>>()
        lessons.subscribe(lessonsSubscriber)
        lessonsSubscriber.assertComplete()
        lessonsSubscriber.assertNoErrors()
    }

    @Test fun gradesTest() {
        val grades = mobile.getGrades(student.classId, student.classificationPeriodId, student.id)
        val gradesSubscriber = TestObserver<List<Grade>>()
        grades.subscribe(gradesSubscriber)
        gradesSubscriber.assertComplete()
        gradesSubscriber.assertNoErrors()
    }

    @Test fun examsTest() {
        val exams = mobile.getExams("2018-05-28", "2018-06-03", student.classId, student.classificationPeriodId, student.id)
        val examsSubscriber = TestObserver<List<Exam>>()
        exams.subscribe(examsSubscriber)
        examsSubscriber.assertComplete()
        examsSubscriber.assertNoErrors()
    }

    @Test fun notesTest() {
        val notes = mobile.getNotes(student.classificationPeriodId, student.id)
        val notesSubscriber = TestObserver<List<Note>>()
        notes.subscribe(notesSubscriber)
        notesSubscriber.assertComplete()
        notesSubscriber.assertNoErrors()
    }

    @Test fun attendanceTest() {
        val attendance = mobile.getAttendance("2018-04-23", "2018-04-24", student.classId, student.classificationPeriodId, student.id)
        val attendanceSubscriber = TestObserver<List<Attendance>>()
        attendance.subscribe(attendanceSubscriber)
        attendanceSubscriber.assertComplete()
        attendanceSubscriber.assertNoErrors()
    }

    @Test fun homeworkTest() {
        val homework = mobile.getHomework("2017-10-23", "2017-10-27", student.classId, student.classificationPeriodId, student.id)
        val homeworkSubscriber = TestObserver<List<Homework>>()
        homework.subscribe(homeworkSubscriber)
        homeworkSubscriber.assertComplete()
        homeworkSubscriber.assertNoErrors()
    }
}
