package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.attendance.AttendanceResponse
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
import org.junit.Test

const val DEVICE_NAME = "Wulkanowy#client"
const val HOST = "https://api.fakelog.cf"
const val SYMBOL = "Default"
const val TOKEN = "FK100000"
const val PIN = "999999"

class UonetTest {

    @Test fun registerTest() {
        // RegisterRepository
        val register = RegisterRepository(HOST, SYMBOL)

        val certificate = register.getCertificate(TOKEN, PIN, DEVICE_NAME)
        val certSubscriber = TestObserver<CertificateResponse>()
        certificate.subscribe(certSubscriber)
        certSubscriber.assertComplete()
        certSubscriber.assertNoErrors()

        assertEquals(false, certSubscriber.values()[0].isError)

        val tokenCrt = certSubscriber.values()[0].tokenCert

        register.signature = tokenCrt.certificatePfx
        register.certificate = tokenCrt.certificateKey

        val pupils = register.getPupils()
        val pupilSubscriber = TestObserver<ApiResponse<List<Student>>>()
        pupils.subscribe(pupilSubscriber)
        pupilSubscriber.assertComplete()
        pupilSubscriber.assertNoErrors()
        assertEquals("Ok", pupilSubscriber.values()[0].status)
        assertEquals(1, pupilSubscriber.values()[0].data!!.size)

        val student = pupilSubscriber.values()[0].data!![0]

        // MobileRepository
        val mobile = MobileRepository(HOST, SYMBOL, register.signature, register.certificate, student.reportingUnitSymbol)

        val start = mobile.logStart()
        val startSubscriber = TestObserver<ApiResponse<String>>()
        start.subscribe(startSubscriber)
        startSubscriber.assertComplete()
        startSubscriber.assertNoErrors()
        assertEquals("Ok", startSubscriber.values()[0].status)

        val dictionaries = mobile.getDictionaries(student.userLoginId, student.classificationPeriodId, student.classId)
        val dictionariesSubscriber = TestObserver<ApiResponse<Dictionaries>>()
        dictionaries.subscribe(dictionariesSubscriber)
        dictionariesSubscriber.assertComplete()
        dictionariesSubscriber.assertNoErrors()
        assertEquals("Ok", dictionariesSubscriber.values()[0].status)

        val lessons = mobile.getTimetable("2018-04-23", "2018-04-24", student.classId, student.classificationPeriodId, student.id)
        val lessonsSubscriber = TestObserver<ApiResponse<List<Lesson>>>()
        lessons.subscribe(lessonsSubscriber)
        lessonsSubscriber.assertComplete()
        lessonsSubscriber.assertNoErrors()
        assertEquals("Ok", lessonsSubscriber.values()[0].status)

        val grades = mobile.getGrades(student.classId, student.classificationPeriodId, student.id)
        val gradesSubscriber = TestObserver<ApiResponse<List<Grade>>>()
        grades.subscribe(gradesSubscriber)
        gradesSubscriber.assertComplete()
        gradesSubscriber.assertNoErrors()
        assertEquals("Ok", gradesSubscriber.values()[0].status)

        val exams = mobile.getExams("2018-05-28", "2018-06-03", student.classId, student.classificationPeriodId, student.id)
        val examsSubscriber = TestObserver<ApiResponse<List<Exam>>>()
        exams.subscribe(examsSubscriber)
        examsSubscriber.assertComplete()
        examsSubscriber.assertNoErrors()
        assertEquals("Ok", examsSubscriber.values()[0].status)

        val notes = mobile.getNotes(student.classificationPeriodId, student.id)
        val notesSubscriber = TestObserver<ApiResponse<List<Note>>>()
        notes.subscribe(notesSubscriber)
        notesSubscriber.assertComplete()
        notesSubscriber.assertNoErrors()
        assertEquals("Ok", notesSubscriber.values()[0].status)

        val attendance = mobile.getAttendance("2018-04-23", "2018-04-24", student.classId, student.classificationPeriodId, student.id)
        val attendanceSubscriber = TestObserver<ApiResponse<AttendanceResponse>>()
        attendance.subscribe(attendanceSubscriber)
        attendanceSubscriber.assertComplete()
        attendanceSubscriber.assertNoErrors()
        assertEquals("Ok", attendanceSubscriber.values()[0].status)
        assertEquals("2018-04-23", attendanceSubscriber.values()[0].data!!.dateStartText)

        val homework = mobile.getHomework("2017-10-23", "2017-10-27", student.classId, student.classificationPeriodId, student.id)
        val homeworkSubscriber = TestObserver<ApiResponse<List<Homework>>>()
        homework.subscribe(homeworkSubscriber)
        homeworkSubscriber.assertComplete()
        homeworkSubscriber.assertNoErrors()
        assertEquals("Ok", homeworkSubscriber.values()[0].status)
    }
}
