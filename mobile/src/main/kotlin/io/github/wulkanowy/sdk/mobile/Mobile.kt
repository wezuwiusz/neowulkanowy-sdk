package io.github.wulkanowy.sdk.mobile

import io.github.wulkanowy.sdk.mobile.attendance.Attendance
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.mobile.exams.Exam
import io.github.wulkanowy.sdk.mobile.grades.Grade
import io.github.wulkanowy.sdk.mobile.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.mobile.homework.Homework
import io.github.wulkanowy.sdk.mobile.messages.Message
import io.github.wulkanowy.sdk.mobile.notes.Note
import io.github.wulkanowy.sdk.mobile.register.Student
import io.github.wulkanowy.sdk.mobile.repository.RegisterRepository
import io.github.wulkanowy.sdk.mobile.repository.RepositoryManager
import io.github.wulkanowy.sdk.mobile.school.Teacher
import io.github.wulkanowy.sdk.mobile.timetable.Lesson
import io.github.wulkanowy.signer.getPrivateKeyFromCert
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class Mobile {

    var apiKey = ""

    var pin = ""

    var token = ""

    var apiBaseUrl = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var deviceName = "Wulkanowy SDK"

    var certKey = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var certificate = ""
        set(value) {
            field = value
            resettableManager.reset()
        }

    var privateKey = ""
        set(value) {
            field = value
            resettableManager.reset()
        }
    var classId = 0
        set(value) {
            field = value
        }

    var studentId = 0
        set(value) {
            field = value
        }

    var schoolSymbol = ""
        set(value) {
            field = value
        }

    var loginId = 0

    var diaryId = 0
        set(value) {
            field = value
        }
    var symbol = ""
        set(value) {
            field = value
        }

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
        }

    private val resettableManager = resettableManager()

    private val serviceManager by resettableLazy(resettableManager) {
        RepositoryManager(logLevel, privateKey, certKey, interceptors, apiBaseUrl, schoolSymbol)
    }

    private val routes by resettableLazy(resettableManager) {
        serviceManager.getRoutesRepository()
    }

    private val mobile by resettableLazy(resettableManager) {
        serviceManager.getMobileRepository()
    }

    fun getRegisterRepo(host: String, symbol: String): RegisterRepository {
        return serviceManager.getRegisterRepository(host, symbol)
    }

    private lateinit var dictionaries: Dictionaries

    fun getDictionaries(): Single<Dictionaries> {
        if (::dictionaries.isInitialized) return Single.just(dictionaries)

        return mobile.getDictionaries(0, 0, 0).map {
            it.apply { dictionaries = this }
        }
    }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false, index: Int = -1) {
        interceptors[index] = interceptor to network
    }

    fun getApiStudents(token: String, pin: String, symbol: String): Single<List<Student>> {
        return routes.getRouteByToken(token).flatMap {
            this@Mobile.apiBaseUrl = it
            this@Mobile.symbol = symbol
            getRegisterRepo(apiBaseUrl, symbol).getCertificate(token, pin, deviceName)
        }.flatMap { certificateResponse ->
            if (certificateResponse.isError) throw RuntimeException(certificateResponse.message)
            this@Mobile.certKey = certificateResponse.tokenCert!!.certificateKey
            this@Mobile.privateKey = getPrivateKeyFromCert(apiKey, certificateResponse.tokenCert.certificatePfx)
            getRegisterRepo(apiBaseUrl, this@Mobile.symbol).getPupils().map { students ->
                students.map {
                    it.copy().apply {
                        privateKey = this@Mobile.privateKey
                        ssl = certificateResponse.tokenCert.apiEndpoint.startsWith("https")
                        certificateKey = certificateResponse.tokenCert.certificateKey
                        apiHost = certificateResponse.tokenCert.apiEndpoint.removeSuffix("/")
                    }
                }
            }
        }
    }

    fun getPupils(): Single<List<Student>> {
        return getRegisterRepo(apiBaseUrl.replace("/$symbol", ""), symbol).getPupils()
    }

    fun getAttendance(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Attendance>> {
        return mobile.getAttendance(start, end, classId, classificationPeriodId, studentId)
    }

    fun getExams(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Exam>> {
        return mobile.getExams(start, end, classId, classificationPeriodId, studentId)
    }

    fun getGrades(classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Grade>> {
        return mobile.getGrades(classId, classificationPeriodId, studentId)
    }

    fun getGradesSummary(classId: Int, classificationPeriodId: Int, studentId: Int): Single<GradesSummaryResponse> {
        return mobile.getGradesSummary(classId, classificationPeriodId, studentId)
    }

    fun getHomework(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Homework>> {
        return mobile.getHomework(start, end, classId, classificationPeriodId, studentId)
    }

    fun getNotes(classificationPeriodId: Int, studentId: Int): Single<List<Note>> {
        return mobile.getNotes(classificationPeriodId, studentId)
    }

    fun getTeachers(studentId: Int, semesterId: Int): Single<List<Teacher>> {
        return mobile.getTeachers(studentId, semesterId)
    }

    fun getMessages(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): Single<List<Message>> {
        return mobile.getMessages(start, end, loginId, studentId)
    }

    fun getMessagesSent(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): Single<List<Message>> {
        return mobile.getMessagesSent(start, end, loginId, studentId)
    }

    fun getMessagesDeleted(start: LocalDateTime, end: LocalDateTime, loginId: Int, studentId: Int): Single<List<Message>> {
        return mobile.getMessagesDeleted(start, end, loginId, studentId)
    }

    fun changeMessageStatus(messageId: Int, folder: String, status: String, loginId: Int, studentId: Int): Single<String> {
        return mobile.changeMessageStatus(messageId, folder, status, loginId, studentId)
    }

    fun getTimetable(start: LocalDate, end: LocalDate, classId: Int, classificationPeriodId: Int, studentId: Int): Single<List<Lesson>> {
        return mobile.getTimetable(start, end, classId, classificationPeriodId, studentId)
    }
}
