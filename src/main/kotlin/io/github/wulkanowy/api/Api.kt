package io.github.wulkanowy.api

import io.github.wulkanowy.api.login.LoginHelper
import io.github.wulkanowy.api.messages.Folder
import io.github.wulkanowy.api.messages.Message
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.repository.HomepageRepository
import io.github.wulkanowy.api.repository.MessagesRepository
import io.github.wulkanowy.api.repository.RegisterRepository
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.github.wulkanowy.api.repository.StudentAndParentStartRepository
import io.github.wulkanowy.api.repository.StudentRepository
import io.github.wulkanowy.api.repository.StudentStartRepository
import io.github.wulkanowy.api.service.ServiceManager
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class Api {

    private val changeManager = resettableManager()

    var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            if (field != value) changeManager.reset()
            field = value
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

    var useNewStudent: Boolean = false

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

    enum class LoginType {
        AUTO,
        STANDARD,
        ADFS,
        ADFSCards,
        ADFSLight,
        ADFSLightScoped
    }

    private val appInterceptors: MutableMap<Int, Pair<Interceptor, Boolean>> = mutableMapOf()

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false, index: Int = -1) {
        appInterceptors[index] = Pair(interceptor, network)
    }

    private val schema by resettableLazy(changeManager) { "http" + if (ssl) "s" else "" }

    private val normalizedSymbol by resettableLazy(changeManager) { if (symbol.isBlank()) "Default" else symbol }

    private val serviceManager by resettableLazy(changeManager) {
        ServiceManager(
            logLevel,
            loginType,
            schema,
            host,
            normalizedSymbol,
            email,
            password,
            schoolSymbol,
            studentId,
            diaryId,
            androidVersion,
            buildTag
        ).apply {
            appInterceptors.forEach {
                setInterceptor(it.value.first, it.value.second, it.key)
            }
        }
    }

    private val register by resettableLazy(changeManager) {
        RegisterRepository(
            normalizedSymbol, email, password, useNewStudent,
            LoginHelper(
                loginType,
                schema,
                host,
                normalizedSymbol,
                serviceManager.getCookieManager(),
                serviceManager.getLoginService()
            ),
            serviceManager.getRegisterService(),
            serviceManager.getSnpService(withLogin = false, interceptor = false),
            serviceManager.getStudentService(withLogin = false, interceptor = false),
            serviceManager.urlGenerator
        )
    }

    private val snpStart by resettableLazy(changeManager) {
        if (0 == studentId) throw ApiException("Student id is not set")
        StudentAndParentStartRepository(
            normalizedSymbol,
            schoolSymbol,
            studentId,
            serviceManager.getSnpService(withLogin = true, interceptor = false)
        )
    }

    private val studentStart by resettableLazy(changeManager) {
        if (0 == studentId) throw ApiException("Student id is not set")
        if (0 == classId) throw ApiException("Class id is not set")
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

    fun getStudents() = register.getStudents()

    fun getSemesters() = if (useNewStudent) studentStart.getSemesters() else snpStart.getSemesters()

    fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getAttendance(startDate, endDate) else snp.getAttendance(startDate, endDate)

    fun getAttendanceSummary(subjectId: Int? = -1) = if (useNewStudent) student.getAttendanceSummary(subjectId) else snp.getAttendanceSummary(subjectId)

    fun getSubjects() = if (useNewStudent) student.getSubjects() else snp.getSubjects()

    fun getExams(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getExams(startDate, endDate) else snp.getExams(startDate, endDate)

    fun getGrades(semesterId: Int? = null) = if (useNewStudent) student.getGrades(semesterId) else snp.getGrades(semesterId)

    fun getGradesSummary(semesterId: Int? = null) = if (useNewStudent) student.getGradesSummary(semesterId) else snp.getGradesSummary(semesterId)

    fun getGradesStatistics(semesterId: Int, annual: Boolean = false) =
        if (useNewStudent) student.getGradesStatistics(semesterId, annual) else snp.getGradesStatistics(semesterId, annual)

    fun getHomework(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getHomework(startDate, endDate) else snp.getHomework(startDate, endDate)

    fun getNotes() = if (useNewStudent) student.getNotes() else snp.getNotes()

    fun getRegisteredDevices() = if (useNewStudent) student.getRegisteredDevices() else snp.getRegisteredDevices()

    fun getToken() = snp.getToken()

    fun unregisterDevice(id: Int) = snp.unregisterDevice(id)

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

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null) =
        if (useNewStudent) student.getTimetable(startDate, endDate) else snp.getTimetable(startDate, endDate)

    fun getCompletedLessons(startDate: LocalDate, endDate: LocalDate? = null, subjectId: Int = -1) =
        if (useNewStudent) student.getCompletedLessons(startDate, endDate, subjectId) else snp.getCompletedLessons(startDate, endDate, subjectId)

    fun getLuckyNumber() = homepage.getLuckyNumber()
}
