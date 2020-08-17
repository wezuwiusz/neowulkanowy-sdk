package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.attendance.Absent
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.messages.Folder
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository
import io.github.wulkanowy.sdk.scrapper.repository.HomepageRepository
import io.github.wulkanowy.sdk.scrapper.repository.MessagesRepository
import io.github.wulkanowy.sdk.scrapper.repository.RegisterRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentStartRepository
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

class Scrapper {

    // TODO: refactor
    enum class LoginType {
        AUTO,
        STANDARD,
        ADFS,
        ADFSCards,
        ADFSLight,
        ADFSLightScoped,
        ADFSLightCufs
    }

    private val changeManager = resettableManager()

    var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var baseUrl: String = "https://fakelog.cf"
        set(value) {
            field = value
            ssl = baseUrl.startsWith("https")
            host = URL(value).let { "${it.host}:${it.port}".removeSuffix(":-1") }
        }

    var ssl: Boolean = true
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var host: String = "fakelog.cf"
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var loginType: LoginType = LoginType.AUTO
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var symbol: String = "Default"
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var email: String = ""
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var password: String = ""
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var schoolSymbol: String = ""
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var studentId: Int = 0
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var classId: Int = 0
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var diaryId: Int = 0
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var schoolYear: Int = 0
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var emptyCookieJarInterceptor: Boolean = false
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    /**
     * @see <a href="https://deviceatlas.com/blog/most-popular-android-smartphones#poland">The most popular Android phones - 2018</a>
     * @see <a href="http://www.tera-wurfl.com/explore/?action=wurfl_id&id=samsung_sm_j500h_ver1">Tera-WURFL Explorer - Samsung SM-J500H (Galaxy J5)</a>
     */
    var androidVersion: String = "5.1"
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var buildTag: String = "SM-J500H Build/LMY48B"
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    private val appInterceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun addInterceptor(interceptor: Interceptor, network: Boolean = false) {
        appInterceptors.add(interceptor to network)
    }

    private val schema by resettableLazy(changeManager) { "http" + if (ssl) "s" else "" }

    private val normalizedSymbol by resettableLazy(changeManager) { if (symbol.isBlank()) "Default" else symbol.getNormalizedSymbol() }

    private val okHttpFactory by lazy { OkHttpClientBuilderFactory() }

    private val serviceManager by resettableLazy(changeManager) {
        ServiceManager(
            okHttpClientBuilderFactory = okHttpFactory,
            logLevel = logLevel,
            loginType = loginType,
            schema = schema,
            host = host,
            symbol = normalizedSymbol,
            email = email,
            password = password,
            schoolSymbol = schoolSymbol,
            studentId = studentId,
            diaryId = diaryId,
            schoolYear = schoolYear,
            androidVersion = androidVersion,
            buildTag = buildTag,
            emptyCookieJarIntercept = emptyCookieJarInterceptor
        ).apply {
            appInterceptors.forEach { (interceptor, isNetwork) ->
                setInterceptor(interceptor, isNetwork)
            }
        }
    }

    private val account by lazy { AccountRepository(serviceManager.getAccountService()) }

    private val register by resettableLazy(changeManager) {
        RegisterRepository(
            normalizedSymbol, email, password,
            LoginHelper(loginType, schema, host, normalizedSymbol, serviceManager.getCookieManager(), serviceManager.getLoginService()),
            serviceManager.getRegisterService(),
            serviceManager.getMessagesService(),
            serviceManager.getStudentService(withLogin = false, studentInterceptor = false), // it is really needed?
            serviceManager.urlGenerator
        )
    }

    private val studentStart by resettableLazy(changeManager) {
        if (0 == studentId) throw ScrapperException("Student id is not set")
        if (0 == classId) throw ScrapperException("Class id is not set")
        StudentStartRepository(
            studentId = studentId,
            classId = classId,
            api = serviceManager.getStudentService(withLogin = true, studentInterceptor = false)
        )
    }

    private val student by resettableLazy(changeManager) {
        StudentRepository(serviceManager.getStudentService())
    }

    private val messages by resettableLazy(changeManager) {
        MessagesRepository(serviceManager.getMessagesService())
    }

    private val homepage by resettableLazy(changeManager) {
        HomepageRepository(serviceManager.getHomepageService())
    }

    suspend fun getPasswordResetCaptcha(registerBaseUrl: String, symbol: String) = account.getPasswordResetCaptcha(registerBaseUrl, symbol)

    suspend fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): String {
        return account.sendPasswordResetRequest(registerBaseUrl, symbol, email.trim(), captchaCode)
    }

    suspend fun getStudents() = register.getStudents()

    suspend fun getSemesters() = studentStart.getSemesters()

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null) = student.getAttendance(startDate, endDate)

    suspend fun getAttendanceSummary(subjectId: Int? = -1) = student.getAttendanceSummary(subjectId)

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null) = student.excuseForAbsence(absents, content)

    suspend fun getSubjects() = student.getSubjects()

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate? = null) = student.getExams(startDate, endDate)

    suspend fun getGrades(semesterId: Int) = student.getGrades(semesterId)

    suspend fun getGradesDetails(semesterId: Int? = null) = student.getGradesDetails(semesterId)

    suspend fun getGradesSummary(semesterId: Int? = null) = student.getGradesSummary(semesterId)

    suspend fun getGradesPartialStatistics(semesterId: Int) = student.getGradesPartialStatistics(semesterId)

    suspend fun getGradesPointsStatistics(semesterId: Int) = student.getGradesPointsStatistics(semesterId)

    suspend fun getGradesAnnualStatistics(semesterId: Int) = student.getGradesAnnualStatistics(semesterId)

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate? = null) = student.getHomework(startDate, endDate)

    suspend fun getNotes() = student.getNotes()

    suspend fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null) = student.getTimetable(startDate, endDate)

    suspend fun getCompletedLessons(startDate: LocalDate, endDate: LocalDate? = null, subjectId: Int = -1) = student.getCompletedLessons(startDate, endDate, subjectId)

    suspend fun getRegisteredDevices() = student.getRegisteredDevices()

    suspend fun getToken() = student.getToken()

    suspend fun unregisterDevice(id: Int) = student.unregisterDevice(id)

    suspend fun getTeachers() = student.getTeachers()

    suspend fun getSchool() = student.getSchool()

    suspend fun getStudentInfo() = student.getStudentInfo()

    suspend fun getReportingUnits() = messages.getReportingUnits()

    suspend fun getRecipients(unitId: Int, role: Int = 2) = messages.getRecipients(unitId, role)

    suspend fun getMessages(
        folder: Folder,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null
    ): List<Message> {
        return when (folder) {
            Folder.RECEIVED -> messages.getReceivedMessages(startDate, endDate)
            Folder.SENT -> messages.getSentMessages(startDate, endDate)
            Folder.TRASHED -> messages.getDeletedMessages(startDate, endDate)
        }
    }

    suspend fun getReceivedMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null) = messages.getReceivedMessages(startDate, endDate)

    suspend fun getSentMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null) = messages.getSentMessages(startDate, endDate)

    suspend fun getDeletedMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null) = messages.getDeletedMessages(startDate, endDate)

    suspend fun getMessageRecipients(messageId: Int, loginId: Int = 0) = messages.getMessageRecipients(messageId, loginId)

    suspend fun getMessageDetails(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null) = messages.getMessageDetails(messageId, folderId, read, id)

    suspend fun getMessageContent(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null) = messages.getMessage(messageId, folderId, read, id)

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>) = messages.sendMessage(subject, content, recipients)

    suspend fun deleteMessages(messagesToDelete: List<Pair<Int, Int>>) = messages.deleteMessages(messagesToDelete)

    suspend fun getSelfGovernments() = homepage.getSelfGovernments()

    suspend fun getStudentThreats() = homepage.getStudentThreats()

    suspend fun getStudentsTrips() = homepage.getStudentsTrips()

    suspend fun getLastGrades() = homepage.getLastGrades()

    suspend fun getFreeDays() = homepage.getFreeDays()

    suspend fun getKidsLuckyNumbers() = homepage.getKidsLuckyNumbers()

    suspend fun getKidsLessonPlan() = homepage.getKidsLessonPlan()

    suspend fun getLastHomework() = homepage.getLastHomework()

    suspend fun getLastTests() = homepage.getLastTests()

    suspend fun getLastStudentLessons() = homepage.getLastStudentLessons()
}
