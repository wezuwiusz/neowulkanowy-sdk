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
import io.github.wulkanowy.sdk.scrapper.repository.StudentAndParentRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentAndParentStartRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentStartRepository
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.net.URL

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

    var useNewStudent: Boolean = true

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
        ServiceManager(okHttpFactory, logLevel, loginType, schema, host, normalizedSymbol, email, password, schoolSymbol, studentId, diaryId, schoolYear, androidVersion, buildTag)
            .apply {
                appInterceptors.forEach { (interceptor, isNetwork) ->
                    setInterceptor(interceptor, isNetwork)
                }
            }
    }

    private val account by lazy { AccountRepository(serviceManager.getAccountService()) }

    private val register by resettableLazy(changeManager) {
        RegisterRepository(
            normalizedSymbol, email, password, useNewStudent,
            LoginHelper(loginType, schema, host, normalizedSymbol, serviceManager.getCookieManager(), serviceManager.getLoginService()),
            serviceManager.getRegisterService(),
            serviceManager.getSnpService(withLogin = false, interceptor = false),
            serviceManager.getStudentService(withLogin = false, interceptor = false),
            serviceManager.urlGenerator
        )
    }

    private val snpStart by resettableLazy(changeManager) {
        if (0 == studentId) throw ScrapperException("Student id is not set")
        StudentAndParentStartRepository(normalizedSymbol, schoolSymbol, studentId, serviceManager.getSnpService(withLogin = true, interceptor = false))
    }

    private val studentStart by resettableLazy(changeManager) {
        if (0 == studentId) throw ScrapperException("Student id is not set")
        if (0 == classId) throw ScrapperException("Class id is not set")
        StudentStartRepository(studentId, classId, serviceManager.getStudentService(withLogin = true, interceptor = false))
    }

    private val snp by resettableLazy(changeManager) {
        StudentAndParentRepository(serviceManager.getSnpService())
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

    fun getPasswordResetCaptcha(registerBaseUrl: String, symbol: String) = account.getPasswordResetCaptcha(registerBaseUrl, symbol)

    fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): Single<Pair<Boolean, String>> {
        return account.sendPasswordResetRequest(registerBaseUrl, symbol, email, captchaCode)
    }

    fun getStudents() = register.getStudents()

    fun getSemesters() = if (useNewStudent) studentStart.getSemesters() else snpStart.getSemesters()

    fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getAttendance(startDate, endDate) else snp.getAttendance(startDate, endDate)

    fun getAttendanceSummary(subjectId: Int? = -1) = if (useNewStudent) student.getAttendanceSummary(subjectId) else snp.getAttendanceSummary(subjectId)

    fun excuseForAbsence(absents: List<Absent>, content: String? = null) = student.excuseForAbsence(absents, content)

    fun getSubjects() = if (useNewStudent) student.getSubjects() else snp.getSubjects()

    fun getExams(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getExams(startDate, endDate) else snp.getExams(startDate, endDate)

    fun getGrades(semesterId: Int? = null) = if (useNewStudent) student.getGrades(semesterId) else snp.getGrades(semesterId)

    fun getGradesSummary(semesterId: Int? = null) = if (useNewStudent) student.getGradesSummary(semesterId) else snp.getGradesSummary(semesterId)

    fun getGradesPartialStatistics(semesterId: Int) =
        if (useNewStudent) student.getGradesPartialStatistics(semesterId) else snp.getGradesStatistics(semesterId, false)

    fun getGradesPointsStatistics(semesterId: Int) = student.getGradesPointsStatistics(semesterId)

    fun getGradesAnnualStatistics(semesterId: Int) =
        if (useNewStudent) student.getGradesAnnualStatistics(semesterId) else snp.getGradesStatistics(semesterId, true)

    fun getHomework(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getHomework(startDate, endDate) else snp.getHomework(startDate, endDate)

    fun getNotes() = if (useNewStudent) student.getNotes() else snp.getNotes()

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getTimetable(startDate, endDate) else snp.getTimetable(startDate, endDate)

    fun getCompletedLessons(startDate: LocalDate, endDate: LocalDate? = null, subjectId: Int = -1) =
        if (useNewStudent) student.getCompletedLessons(startDate, endDate, subjectId) else snp.getCompletedLessons(startDate, endDate, subjectId)

    fun getRegisteredDevices() = if (useNewStudent) student.getRegisteredDevices() else snp.getRegisteredDevices()

    fun getToken() = if (useNewStudent) student.getToken() else snp.getToken()

    fun unregisterDevice(id: Int) = if (useNewStudent) student.unregisterDevice(id) else snp.unregisterDevice(id)

    fun getTeachers() = if (useNewStudent) student.getTeachers() else snp.getTeachers()

    fun getSchool() = if (useNewStudent) student.getSchool() else snp.getSchool()

    fun getStudentInfo() = snp.getStudentInfo()

    fun getReportingUnits() = messages.getReportingUnits()

    fun getRecipients(unitId: Int, role: Int = 2) = messages.getRecipients(unitId, role)

    fun getMessages(
        folder: Folder,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null
    ): Single<List<Message>> {
        return when (folder) {
            Folder.RECEIVED -> messages.getReceivedMessages(startDate, endDate)
            Folder.SENT -> messages.getSentMessages(startDate, endDate)
            Folder.TRASHED -> messages.getDeletedMessages(startDate, endDate)
        }
    }

    fun getReceivedMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null) = messages.getReceivedMessages(startDate, endDate)

    fun getSentMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null) = messages.getSentMessages(startDate, endDate)

    fun getDeletedMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null) = messages.getDeletedMessages(startDate, endDate)

    fun getMessageRecipients(messageId: Int, loginId: Int = 0) = messages.getMessageRecipients(messageId, loginId)

    fun getMessageContent(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null) = messages.getMessage(messageId, folderId, read, id)

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>) = messages.sendMessage(subject, content, recipients)

    fun deleteMessages(messages: List<Pair<Int, Int>>) = this.messages.deleteMessages(messages)

    @Deprecated("Deprecated due to VULCAN homepage update 19.06", ReplaceWith("getKidsLuckyNumbers()"))
    fun getLuckyNumber() = homepage.getLuckyNumber()

    fun getSelfGovernments() = homepage.getSelfGovernments()

    fun getStudentsTrips() = homepage.getStudentsTrips()

    fun getLastGrades() = homepage.getLastGrades()

    fun getFreeDays() = homepage.getFreeDays()

    fun getKidsLuckyNumbers() = homepage.getKidsLuckyNumbers()

    fun getKidsLessonPlan() = homepage.getKidsLessonPlan()

    fun getLastHomework() = homepage.getLastHomework()

    fun getLastTests() = homepage.getLastTests()

    fun getLastStudentLessons() = homepage.getLastStudentLessons()
}
