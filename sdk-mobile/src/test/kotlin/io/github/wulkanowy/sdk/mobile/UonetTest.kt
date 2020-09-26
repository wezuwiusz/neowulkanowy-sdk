package io.github.wulkanowy.sdk.mobile

import io.github.wulkanowy.sdk.mobile.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.mobile.repository.MobileRepository
import io.github.wulkanowy.sdk.mobile.repository.RegisterRepository
import io.github.wulkanowy.signer.getPrivateKeyFromCert
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.time.LocalDate.of

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
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
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

            val certificate = runBlocking { register.getCertificate(TOKEN, PIN, DEVICE_NAME, "8.1.0", "") }

            assertEquals(false, certificate.isError)

            val tokenCrt = certificate.tokenCert

            val certKey = tokenCrt!!.certificateKey
            val cert = tokenCrt.certificatePfx

            val privateKey = getPrivateKeyFromCert(PASSWORD, cert)

            val pupils = runBlocking { register.getStudents() }
            assertEquals(2, pupils.size)

            student = pupils[0]

            // MobileRepository
            mobile = MobileRepository(getRetrofitBuilder(privateKey, certKey)
                .baseUrl("$HOST/powiatwulkanowy/${student.reportingUnitSymbol}/mobile-api/Uczen.v3.Uczen/")
                .build().create()
            )
        }
    }

    @Test
    fun logStartTest() {
        val start = runBlocking { mobile.logStart() }
        assertEquals("Ok", start.status)
    }

    @Test
    fun dictionariesTest() {
        runBlocking { mobile.getDictionaries(student.userLoginId, student.classificationPeriodId, student.classId) }
    }

    @Test
    fun timetableTest() {
        runBlocking { mobile.getTimetable(of(2018, 4, 23), of(2018, 4, 24), student.classId, student.classificationPeriodId, student.id) }
    }

    @Test
    fun gradesTest() {
        runBlocking { mobile.getGradesDetails(student.classId, student.classificationPeriodId, student.id) }
    }

    @Test
    fun examsTest() {
        runBlocking { mobile.getExams(of(2018, 5, 28), of(2018, 6, 3), student.classId, student.classificationPeriodId, student.id) }
    }

    @Test
    fun notesTest() {
        runBlocking { mobile.getNotes(student.classificationPeriodId, student.id) }
    }

    @Test
    fun attendanceTest() {
        runBlocking { mobile.getAttendance(of(2018, 4, 23), of(2018, 4, 24), student.classId, student.classificationPeriodId, student.id) }
    }

    @Test
    fun homeworkTest() {
        runBlocking { mobile.getHomework(of(2017, 10, 23), of(2017, 10, 27), student.classId, student.classificationPeriodId, student.id) }
    }
}
