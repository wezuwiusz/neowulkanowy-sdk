package io.github.wulkanowy.sdk.mobile

import io.github.wulkanowy.sdk.mobile.attendance.Attendance
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.mobile.exams.Exam
import io.github.wulkanowy.sdk.mobile.grades.Grade
import io.github.wulkanowy.sdk.mobile.homework.Homework
import io.github.wulkanowy.sdk.mobile.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.mobile.notes.Note
import io.github.wulkanowy.sdk.mobile.register.CertificateResponse
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.mobile.repository.MobileRepository
import io.github.wulkanowy.sdk.mobile.repository.RegisterRepository
import io.github.wulkanowy.sdk.mobile.timetable.Lesson
import io.github.wulkanowy.signer.getPrivateKeyFromCert
import io.reactivex.observers.TestObserver
import junit.framework.TestCase.assertEquals
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.threeten.bp.LocalDate.of
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

const val PASSWORD = "012345678901234567890123456789AB"
const val DEVICE_NAME = "Wulkanowy#client"
const val HOST = "https://api.fakelog.cf"
const val SYMBOL = "powiatwulkanowy"
const val TOKEN = "FK100000"
const val PIN = "999999"

@Ignore
class UonetTest {

    companion object {

        private lateinit var mobile: MobileRepository

        private lateinit var student: Student

        private fun getRetrofitBuilder(privateKey: String, certKey: String): Retrofit.Builder {
            return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .addInterceptor(SignInterceptor(privateKey, certKey))
                    .build()
                )
        }

        @JvmStatic
        @BeforeClass
        fun setUp() {
            // RegisterRepository
            val register = RegisterRepository(getRetrofitBuilder("", "")
                .baseUrl("$HOST/$SYMBOL/mobile-api/Uczen.v3.UczenStart/")
                .build().create()
            )

            val certificate = register.getCertificate(TOKEN, PIN, DEVICE_NAME, "8.1.0")
            val certSubscriber = TestObserver<CertificateResponse>()
            certificate.subscribe(certSubscriber)
            certSubscriber.assertComplete()
            certSubscriber.assertNoErrors()

            assertEquals(false, certSubscriber.values()[0].isError)

            val tokenCrt = certSubscriber.values()[0].tokenCert

            val certKey = tokenCrt!!.certificateKey
            val cert = tokenCrt.certificatePfx

            val privateKey = getPrivateKeyFromCert(PASSWORD, cert)

            val pupils = register.getStudents()
            val pupilSubscriber = TestObserver<List<Student>>()
            pupils.subscribe(pupilSubscriber)
            pupilSubscriber.assertComplete()
            pupilSubscriber.assertNoErrors()
            assertEquals(2, pupilSubscriber.values()[0].size)

            student = pupilSubscriber.values()[0][0]

            // MobileRepository
            mobile = MobileRepository(getRetrofitBuilder(privateKey, certKey)
                .baseUrl("$HOST/powiatwulkanowy/${student.reportingUnitSymbol}/mobile-api/Uczen.v3.Uczen/")
                .build().create()
            )
        }
    }

    @Test
    fun logStartTest() {
        val start = mobile.logStart()
        val startSubscriber = TestObserver<ApiResponse<String>>()
        start.subscribe(startSubscriber)
        startSubscriber.assertComplete()
        startSubscriber.assertNoErrors()
        assertEquals("Ok", startSubscriber.values()[0].status)
    }

    @Test
    fun dictionariesTest() {
        val dictionaries = mobile.getDictionaries(student.userLoginId, student.classificationPeriodId, student.classId)
        val dictionariesSubscriber = TestObserver<Dictionaries>()
        dictionaries.subscribe(dictionariesSubscriber)
        dictionariesSubscriber.assertComplete()
        dictionariesSubscriber.assertNoErrors()
    }

    @Test
    fun timetableTest() {
        val lessons = mobile.getTimetable(of(2018, 4, 23), of(2018, 4, 24), student.classId, student.classificationPeriodId, student.id)
        val lessonsSubscriber = TestObserver<List<Lesson>>()
        lessons.subscribe(lessonsSubscriber)
        lessonsSubscriber.assertComplete()
        lessonsSubscriber.assertNoErrors()
    }

    @Test
    fun gradesTest() {
        val grades = mobile.getGradesDetails(student.classId, student.classificationPeriodId, student.id)
        val gradesSubscriber = TestObserver<List<Grade>>()
        grades.subscribe(gradesSubscriber)
        gradesSubscriber.assertComplete()
        gradesSubscriber.assertNoErrors()
    }

    @Test
    fun examsTest() {
        val exams = mobile.getExams(of(2018, 5, 28), of(2018, 6, 3), student.classId, student.classificationPeriodId, student.id)
        val examsSubscriber = TestObserver<List<Exam>>()
        exams.subscribe(examsSubscriber)
        examsSubscriber.assertComplete()
        examsSubscriber.assertNoErrors()
    }

    @Test
    fun notesTest() {
        val notes = mobile.getNotes(student.classificationPeriodId, student.id)
        val notesSubscriber = TestObserver<List<Note>>()
        notes.subscribe(notesSubscriber)
        notesSubscriber.assertComplete()
        notesSubscriber.assertNoErrors()
    }

    @Test
    fun attendanceTest() {
        val attendance = mobile.getAttendance(of(2018, 4, 23), of(2018, 4, 24), student.classId, student.classificationPeriodId, student.id)
        val attendanceSubscriber = TestObserver<List<Attendance>>()
        attendance.subscribe(attendanceSubscriber)
        attendanceSubscriber.assertComplete()
        attendanceSubscriber.assertNoErrors()
    }

    @Test
    fun homeworkTest() {
        val homework = mobile.getHomework(of(2017, 10, 23), of(2017, 10, 27), student.classId, student.classificationPeriodId, student.id)
        val homeworkSubscriber = TestObserver<List<Homework>>()
        homework.subscribe(homeworkSubscriber)
        homeworkSubscriber.assertComplete()
        homeworkSubscriber.assertNoErrors()
    }
}
