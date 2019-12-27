package io.github.wulkanowy.sdk.mobile

import com.migcomponents.migbase64.Base64
import io.github.wulkanowy.sdk.mobile.attendance.Attendance
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.mobile.exams.Exam
import io.github.wulkanowy.sdk.mobile.exception.InvalidPinException
import io.github.wulkanowy.sdk.mobile.exception.InvalidTokenException
import io.github.wulkanowy.sdk.mobile.exception.TokenDeadException
import io.github.wulkanowy.sdk.mobile.grades.Grade
import io.github.wulkanowy.sdk.mobile.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.mobile.homework.Homework
import io.github.wulkanowy.sdk.mobile.messages.Message
import io.github.wulkanowy.sdk.mobile.notes.Note
import io.github.wulkanowy.sdk.mobile.register.CertificateResponse
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.mobile.repository.RepositoryManager
import io.github.wulkanowy.sdk.mobile.school.Teacher
import io.github.wulkanowy.sdk.mobile.timetable.Lesson
import io.github.wulkanowy.signer.getPrivateKeyFromCert
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.nio.charset.Charset

class Mobile {

    var classId = 0

    var studentId = 0

    var loginId = 0

    private val resettableManager = resettableManager()

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
            resettableManager.reset()
        }

    var privateKey = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var certKey = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var baseUrl = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var schoolSymbol = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    private val serviceManager by resettableLazy(resettableManager) { RepositoryManager(logLevel, privateKey, certKey, interceptors, baseUrl, schoolSymbol) }

    private val routes by resettableLazy(resettableManager) { serviceManager.getRoutesRepository() }

    private val mobile by resettableLazy(resettableManager) { serviceManager.getMobileRepository() }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false) {
        interceptors.add(interceptor to network)
    }

    private lateinit var dictionaries: Dictionaries

    fun getDictionaries(): Single<Dictionaries> {
        if (::dictionaries.isInitialized) return Single.just(dictionaries)

        return mobile.getDictionaries(0, 0, classId).map {
            it.apply { dictionaries = this }
        }
    }

    fun getCertificate(token: String, pin: String, symbol: String, deviceName: String, androidVersion: String): Single<CertificateResponse> {
        return routes.getRouteByToken(token).flatMap { baseUrl ->
            serviceManager.getRegisterRepository(baseUrl, symbol).getCertificate(token, pin, deviceName, androidVersion)
        }
    }

    fun getStudents(certRes: CertificateResponse, apiKey: String = ""): Single<List<Student>> {
        if (certRes.isError) when {
            certRes.message == "TokenDead" -> throw TokenDeadException(certRes.message)
            certRes.message?.startsWith("Podany numer PIN jest niepoprawny") == true -> throw InvalidPinException(certRes.message.orEmpty())
            else -> throw InvalidTokenException(certRes.message.orEmpty())
        }

        val cert = certRes.tokenCert!!
        certKey = cert.certificateKey
        baseUrl = cert.baseUrl.removeSuffix("/")
        privateKey = getPrivateKeyFromCert(apiKey.ifEmpty {
            Base64.decode(if (cert.baseUrl.contains("fakelog")) "KDAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OUFCKQ==" else "KENFNzVFQTU5OEM3NzQzQUQ5QjBCNzMyOERFRDg1QjA2KQ==")
                .toString(Charset.defaultCharset())
                .removeSurrounding("(", ")")
        }, cert.certificatePfx)

        return serviceManager.getRegisterRepository(cert.baseUrl).getStudents().map { students ->
            students.map {
                it.copy().apply {
                    certificateKey = this@Mobile.certKey
                    privateKey = this@Mobile.privateKey
                    mobileBaseUrl = this@Mobile.baseUrl
                }
            }
        }
    }

    fun getStudents(): Single<List<Student>> {
        return serviceManager.getRegisterRepository(baseUrl).getStudents()
    }

    fun getAttendance(start: LocalDate, end: LocalDate, classificationPeriodId: Int): Single<List<Attendance>> {
        return mobile.getAttendance(start, end, classId, classificationPeriodId, studentId)
    }

    fun getExams(start: LocalDate, end: LocalDate, classificationPeriodId: Int): Single<List<Exam>> {
        return mobile.getExams(start, end, classId, classificationPeriodId, studentId)
    }

    fun getGrades(classificationPeriodId: Int): Single<List<Grade>> {
        return mobile.getGrades(classId, classificationPeriodId, studentId)
    }

    fun getGradesSummary(classificationPeriodId: Int): Single<GradesSummaryResponse> {
        return mobile.getGradesSummary(classId, classificationPeriodId, studentId)
    }

    fun getHomework(start: LocalDate, end: LocalDate, classificationPeriodId: Int): Single<List<Homework>> {
        return mobile.getHomework(start, end, classId, classificationPeriodId, studentId)
    }

    fun getNotes(classificationPeriodId: Int): Single<List<Note>> {
        return mobile.getNotes(classificationPeriodId, studentId)
    }

    fun getTeachers(studentId: Int, semesterId: Int): Single<List<Teacher>> {
        return mobile.getTeachers(studentId, semesterId)
    }

    fun getMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return mobile.getMessages(start, end, loginId, studentId)
    }

    fun getMessagesSent(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return mobile.getMessagesSent(start, end, loginId, studentId)
    }

    fun getMessagesDeleted(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return mobile.getMessagesDeleted(start, end, loginId, studentId)
    }

    fun changeMessageStatus(messageId: Int, folder: String, status: String): Single<String> {
        return mobile.changeMessageStatus(messageId, folder, status, loginId, studentId)
    }

    fun getTimetable(start: LocalDate, end: LocalDate, classificationPeriodId: Int): Single<List<Lesson>> {
        return mobile.getTimetable(start, end, classId, classificationPeriodId, studentId)
    }
}
