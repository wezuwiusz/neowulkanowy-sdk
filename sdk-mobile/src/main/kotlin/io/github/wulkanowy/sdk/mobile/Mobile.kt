package io.github.wulkanowy.sdk.mobile

import com.migcomponents.migbase64.Base64
import io.github.wulkanowy.sdk.mobile.attendance.Attendance
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.mobile.exams.Exam
import io.github.wulkanowy.sdk.mobile.exception.InvalidPinException
import io.github.wulkanowy.sdk.mobile.exception.NoStudentsException
import io.github.wulkanowy.sdk.mobile.exception.TokenDeadException
import io.github.wulkanowy.sdk.mobile.exception.TokenNotFoundException
import io.github.wulkanowy.sdk.mobile.exception.UnknownTokenException
import io.github.wulkanowy.sdk.mobile.exception.UnsupportedTokenException
import io.github.wulkanowy.sdk.mobile.grades.Grade
import io.github.wulkanowy.sdk.mobile.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.mobile.homework.Homework
import io.github.wulkanowy.sdk.mobile.messages.Message
import io.github.wulkanowy.sdk.mobile.messages.Recipient
import io.github.wulkanowy.sdk.mobile.notes.Note
import io.github.wulkanowy.sdk.mobile.register.CertificateResponse
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.mobile.repository.RepositoryManager
import io.github.wulkanowy.sdk.mobile.school.Teacher
import io.github.wulkanowy.sdk.mobile.timetable.Lesson
import io.github.wulkanowy.signer.getPrivateKeyFromCert
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime

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

    suspend fun getDictionaries(): Dictionaries {
        if (::dictionaries.isInitialized) return dictionaries

        return mobile.getDictionaries(0, 0, classId)
            .apply { dictionaries = this }
    }

    suspend fun getCertificate(token: String, pin: String, symbol: String, deviceName: String, androidVer: String, firebaseToken: String): CertificateResponse {
        val baseUrl = routes.getRouteByToken(token)
        return serviceManager.getRegisterRepository(baseUrl, symbol).getCertificate(token, pin, deviceName, androidVer, firebaseToken)
    }

    suspend fun getStudents(certRes: CertificateResponse, apiKey: String = ""): List<Student> {
        if (certRes.isError) {
            when {
                certRes.message == "TokenDead" -> throw TokenDeadException(certRes.message)
                certRes.message == "TokenNotFound" -> throw TokenNotFoundException(certRes.message)
                certRes.message?.startsWith("Podany numer PIN jest niepoprawny") == true -> throw InvalidPinException(certRes.message)
                certRes.message?.startsWith("Trzykrotnie wpisano niepoprawny kod PIN") == true -> throw InvalidPinException(certRes.message)
                certRes.message == "NoPupils" -> throw NoStudentsException(certRes.message)
                certRes.message == "OnlyKindergarten" -> throw UnsupportedTokenException(certRes.message)
                else -> throw UnknownTokenException(certRes.message.orEmpty())
            }
        }

        val cert = certRes.tokenCert!!
        val privateKeyValue = apiKey.ifEmpty {
            Base64.decode(if (cert.baseUrl.contains("fakelog")) "KDAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OUFCKQ==" else "KENFNzVFQTU5OEM3NzQzQUQ5QjBCNzMyOERFRDg1QjA2KQ==")
                .toString(Charset.defaultCharset())
                .removeSurrounding("(", ")")
        }
        certKey = cert.certificateKey
        baseUrl = cert.baseUrl.removeSuffix("/")
        privateKey = getPrivateKeyFromCert(privateKeyValue, cert.certificatePfx)

        return serviceManager.getRegisterRepository(cert.baseUrl).getStudents().map {
            it.copy().apply {
                certificateKey = this@Mobile.certKey
                privateKey = this@Mobile.privateKey
                mobileBaseUrl = this@Mobile.baseUrl
            }
        }
    }

    suspend fun getStudents(): List<Student> {
        return serviceManager.getRegisterRepository(baseUrl).getStudents()
    }

    suspend fun getAttendance(start: LocalDate, end: LocalDate, classificationPeriodId: Int): List<Attendance> {
        return mobile.getAttendance(start, end, classId, classificationPeriodId, studentId)
    }

    suspend fun getExams(start: LocalDate, end: LocalDate, classificationPeriodId: Int): List<Exam> {
        return mobile.getExams(start, end, classId, classificationPeriodId, studentId)
    }

    suspend fun getGrades(classificationPeriodId: Int): Pair<List<Grade>, GradesSummaryResponse> {
        return getGradesDetails(classificationPeriodId) to getGradesSummary(classificationPeriodId)
    }

    suspend fun getGradesDetails(classificationPeriodId: Int): List<Grade> {
        return mobile.getGradesDetails(classId, classificationPeriodId, studentId)
    }

    suspend fun getGradesSummary(classificationPeriodId: Int): GradesSummaryResponse {
        return mobile.getGradesSummary(classId, classificationPeriodId, studentId)
    }

    suspend fun getHomework(start: LocalDate, end: LocalDate, classificationPeriodId: Int): List<Homework> {
        return mobile.getHomework(start, end, classId, classificationPeriodId, studentId)
    }

    suspend fun getNotes(classificationPeriodId: Int): List<Note> {
        return mobile.getNotes(classificationPeriodId, studentId)
    }

    suspend fun getTeachers(studentId: Int, semesterId: Int): List<Teacher> {
        return mobile.getTeachers(studentId, semesterId)
    }

    suspend fun getMessages(start: LocalDateTime, end: LocalDateTime): List<Message> {
        return mobile.getMessages(start, end, loginId, studentId)
    }

    suspend fun getMessagesSent(start: LocalDateTime, end: LocalDateTime): List<Message> {
        return mobile.getMessagesSent(start, end, loginId, studentId)
    }

    suspend fun getMessagesDeleted(start: LocalDateTime, end: LocalDateTime): List<Message> {
        return mobile.getMessagesDeleted(start, end, loginId, studentId)
    }

    suspend fun changeMessageStatus(messageId: String, folder: String, status: String): String {
        return mobile.changeMessageStatus(messageId, folder, status, loginId, studentId)
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Message {
        val sender = getStudents().singleOrNull { it.loginId == loginId }?.name.orEmpty()
        return mobile.sendMessage(sender, subject, content, recipients, loginId, studentId)
    }

    suspend fun getTimetable(start: LocalDate, end: LocalDate, classificationPeriodId: Int): List<Lesson> {
        return mobile.getTimetable(start, end, classId, classificationPeriodId, studentId)
    }
}
