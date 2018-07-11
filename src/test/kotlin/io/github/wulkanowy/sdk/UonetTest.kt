package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.base.ApiResponse
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.exams.Exam
import io.github.wulkanowy.sdk.grades.Grade
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.github.wulkanowy.sdk.repository.MobileRepository
import io.github.wulkanowy.sdk.repository.RegisterRepository
import io.github.wulkanowy.sdk.timetable.Lesson
import junit.framework.TestCase.assertEquals
import org.junit.Test
import rx.observers.TestSubscriber

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
        val certSubscriber = TestSubscriber<CertificateResponse>()
        certificate.subscribe(certSubscriber)
        certSubscriber.assertCompleted()
        certSubscriber.assertNoErrors()
        assertEquals(false, certSubscriber.onNextEvents[0].isError)

        val tokenCrt = certSubscriber.onNextEvents[0].tokenCert

        register.signature = tokenCrt.certificatePfx
        register.certificate = tokenCrt.certificateKey

        val pupils = register.getPupils()
        val pupilSubscriber = TestSubscriber<ApiResponse<List<Student>>>()
        pupils.subscribe(pupilSubscriber)
        pupilSubscriber.assertCompleted()
        pupilSubscriber.assertNoErrors()
        assertEquals("Ok", pupilSubscriber.onNextEvents[0].status)
        assertEquals(1, pupilSubscriber.onNextEvents[0].data!!.size)

        val student = pupilSubscriber.onNextEvents[0].data!![0]

        // MobileRepository
        val mobile = MobileRepository(HOST, SYMBOL, register.signature, register.certificate, student.reportingUnitSymbol)

        val start = mobile.logStart()
        val startSubscriber = TestSubscriber<ApiResponse<String>>()
        start.subscribe(startSubscriber)
        startSubscriber.assertCompleted()
        startSubscriber.assertNoErrors()
        assertEquals("Ok", startSubscriber.onNextEvents[0].status)

        val dictionaries = mobile.getDictionaries(student.userLoginId, student.classificationPeriodId, student.classId)
        val dictionariesSubscriber = TestSubscriber<ApiResponse<Dictionaries>>()
        dictionaries.subscribe(dictionariesSubscriber)
        dictionariesSubscriber.assertCompleted()
        dictionariesSubscriber.assertNoErrors()
        assertEquals("Ok", dictionariesSubscriber.onNextEvents[0].status)

        val lessons = mobile.getTimetable("2018-04-23", "2018-04-24", student.classId, student.classificationPeriodId, student.id)
        val lessonsSubscriber = TestSubscriber<ApiResponse<List<Lesson>>>()
        lessons.subscribe(lessonsSubscriber)
        lessonsSubscriber.assertCompleted()
        lessonsSubscriber.assertNoErrors()
        assertEquals("Ok", lessonsSubscriber.onNextEvents[0].status)

        val grades = mobile.getGrades(student.classId, student.classificationPeriodId, student.id)
        val gradesSubscriber = TestSubscriber<ApiResponse<List<Grade>>>()
        grades.subscribe(gradesSubscriber)
        gradesSubscriber.assertCompleted()
        gradesSubscriber.assertNoErrors()
        assertEquals("Ok", gradesSubscriber.onNextEvents[0].status)

        val exams = mobile.getExams("2018-05-28", "2018-06-03", student.classId, student.classificationPeriodId, student.id)
        val examsSubscriber = TestSubscriber<ApiResponse<List<Exam>>>()
        exams.subscribe(examsSubscriber)
        examsSubscriber.assertCompleted()
        examsSubscriber.assertNoErrors()
        assertEquals("Ok", examsSubscriber.onNextEvents[0].status)
    }
}
