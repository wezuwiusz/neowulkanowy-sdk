package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.attendance.Absent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.exams.Exam
import io.github.wulkanowy.sdk.scrapper.exception.FeatureUnavailableException
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.Grades
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsSemester
import io.github.wulkanowy.sdk.scrapper.home.DirectorInformation
import io.github.wulkanowy.sdk.scrapper.home.GovernmentUnit
import io.github.wulkanowy.sdk.scrapper.home.LastAnnouncement
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.homework.Homework
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.ModuleHeaders
import io.github.wulkanowy.sdk.scrapper.menu.Menu
import io.github.wulkanowy.sdk.scrapper.messages.Folder
import io.github.wulkanowy.sdk.scrapper.messages.Mailbox
import io.github.wulkanowy.sdk.scrapper.messages.MessageDetails
import io.github.wulkanowy.sdk.scrapper.messages.MessageMeta
import io.github.wulkanowy.sdk.scrapper.messages.MessageReplayDetails
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.RegisterUser
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository
import io.github.wulkanowy.sdk.scrapper.repository.HomepageRepository
import io.github.wulkanowy.sdk.scrapper.repository.MessagesRepository
import io.github.wulkanowy.sdk.scrapper.repository.RegisterRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentPlusRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentStartRepository
import io.github.wulkanowy.sdk.scrapper.repository.SymbolRepository
import io.github.wulkanowy.sdk.scrapper.school.School
import io.github.wulkanowy.sdk.scrapper.school.Teacher
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.student.StudentPhoto
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.net.URL
import java.time.LocalDate
import java.util.concurrent.locks.ReentrantLock

class Scrapper {

    // TODO: refactor
    enum class LoginType {
        AUTO,
        STANDARD,
        ADFS,
        ADFSCards,
        ADFSLight,
        ADFSLightScoped,
        ADFSLightCufs,
    }

    private val changeManager = resettableManager()

    private val cookieJarCabinet = CookieJarCabinet()

    var isEduOne = false

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

    var domainSuffix: String = ""
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
            if (field != value) {
                changeManager.reset()
                cookieJarCabinet.onUserChange()
            }
            field = value
        }

    var email: String = ""
        set(value) {
            if (field != value) {
                changeManager.reset()
                cookieJarCabinet.onUserChange()
            }
            field = value
        }

    var password: String = ""
        set(value) {
            if (field != value) {
                changeManager.reset()
                cookieJarCabinet.onUserChange()
            }
            field = value
        }

    var schoolId: String = ""
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

    var unitId: Int = 0
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var kindergartenDiaryId: Int = 0
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

    var userAgentTemplate: String = ""
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var androidVersion: String = "11"
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var buildTag: String = "Redmi Note 8T"
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    val userAgent: String
        get() {
            return try {
                getFormattedString(userAgentTemplate.ifBlank { defaultUserAgentTemplate }, androidVersion, buildTag)
            } catch (e: Throwable) {
                getFormattedString(defaultUserAgentTemplate, androidVersion, buildTag)
            }
        }

    var endpointsMapping: Map<String, Map<String, Map<String, String>>>
        get() = endpointsMap
        set(value) {
            endpointsMap = value
        }

    var vTokenMapping: Map<String, Map<String, Map<String, String>>>
        get() = vTokenMap
        set(value) {
            vTokenMap = value
        }

    var vHeaders: Map<String, Map<String, Map<String, String>>>
        get() = vHeadersMap
        set(value) {
            vHeadersMap = value
        }

    var responseMapping: Map<String, Map<String, Map<String, Map<String, String>>>>
        get() = responseMap
        set(value) {
            responseMap = value
        }

    var vParamsEvaluation: suspend () -> EvaluateHandler
        get() = vParamsRun
        set(value) {
            vParamsRun = value
        }

    internal companion object {
        var endpointsMap: Map<String, Map<String, Map<String, String>>> = ApiEndpointsMap
        var vTokenMap: Map<String, Map<String, Map<String, String>>> = ApiEndpointsVTokenMap
        var vHeadersMap: Map<String, Map<String, Map<String, String>>> = ApiEndpointsVHeaders
        var responseMap: Map<String, Map<String, Map<String, Map<String, String>>>> = ApiEndpointsResponseMapping
        var vParamsRun: suspend () -> EvaluateHandler = { object : EvaluateHandler {} }
    }

    private val appInterceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun addInterceptor(interceptor: Interceptor, network: Boolean = false) {
        appInterceptors.add(interceptor to network)
    }

    private val schema by resettableLazy(changeManager) { "http" + if (ssl) "s" else "" }

    private val normalizedSymbol by resettableLazy(changeManager) { if (symbol.isBlank()) "Default" else symbol.getNormalizedSymbol() }

    private val okHttpFactory by lazy { OkHttpClientBuilderFactory() }

    private val headersByHost: MutableMap<String, ModuleHeaders?> = mutableMapOf()
    private val loginLock = ReentrantLock(true)

    private val serviceManager by resettableLazy(changeManager) {
        ServiceManager(
            okHttpClientBuilderFactory = okHttpFactory,
            cookieJarCabinet = cookieJarCabinet,
            logLevel = logLevel,
            loginType = loginType,
            schema = schema,
            host = host,
            domainSuffix = domainSuffix,
            symbol = normalizedSymbol,
            email = email,
            password = password,
            schoolId = schoolId,
            studentId = studentId,
            diaryId = diaryId,
            kindergartenDiaryId = kindergartenDiaryId,
            schoolYear = schoolYear,
            emptyCookieJarIntercept = emptyCookieJarInterceptor,
            androidVersion = androidVersion,
            buildTag = buildTag,
            userAgentTemplate = userAgentTemplate,
            loginLock = loginLock,
            headersByHost = headersByHost,
        ).apply {
            appInterceptors.forEach { (interceptor, isNetwork) ->
                setInterceptor(interceptor, isNetwork)
            }
        }
    }

    private val symbolRepository by lazy { SymbolRepository(serviceManager.getSymbolService()) }

    private val account by lazy { AccountRepository(serviceManager.getAccountService()) }

    private val register by resettableLazy(changeManager) {
        RegisterRepository(
            startSymbol = normalizedSymbol,
            email = email,
            password = password,
            loginHelper = LoginHelper(
                loginType = loginType,
                schema = schema,
                host = host,
                domainSuffix = domainSuffix,
                symbol = normalizedSymbol,
                cookieJarCabinet = cookieJarCabinet,
                api = serviceManager.getLoginService(),
                urlGenerator = serviceManager.urlGenerator,
            ),
            register = serviceManager.getRegisterService(),
            student = serviceManager.getStudentService(withLogin = false, studentInterceptor = false),
            studentPlus = serviceManager.getStudentPlusService(withLogin = false),
            symbolService = serviceManager.getSymbolService(),
            url = serviceManager.urlGenerator,
        )
    }

    private val studentStart: StudentStartRepository by resettableLazy(changeManager) {
        if (0 == studentId) throw ScrapperException("Student id is not set")
        if (0 == classId && 0 == kindergartenDiaryId) throw ScrapperException("Class id is not set")
        StudentStartRepository(
            studentId = studentId,
            classId = classId,
            unitId = unitId,
            api = serviceManager.getStudentService(withLogin = true, studentInterceptor = false),
            urlGenerator = serviceManager.urlGenerator,
        )
    }

    private val student: StudentRepository by resettableLazy(changeManager) {
        StudentRepository(
            api = serviceManager.getStudentService(),
            urlGenerator = serviceManager.urlGenerator,
        )
    }

    private val studentPlus: StudentPlusRepository by resettableLazy(changeManager) {
        StudentPlusRepository(
            api = serviceManager.getStudentPlusService(),
        )
    }

    private val messages: MessagesRepository by resettableLazy(changeManager) {
        MessagesRepository(
            api = serviceManager.getMessagesService(),
            urlGenerator = serviceManager.urlGenerator,
        )
    }

    private val homepage by resettableLazy(changeManager) {
        HomepageRepository(serviceManager.getHomepageService())
    }

    fun setAdditionalCookieManager(cookieManager: CookieManager) {
        cookieJarCabinet.setAdditionalCookieManager(cookieManager)
    }

    // Unauthorized

    suspend fun isSymbolNotExist(symbol: String): Boolean = symbolRepository.isSymbolNotExist(symbol)

    suspend fun getPasswordResetCaptcha(registerBaseUrl: String, symbol: String): Pair<String, String> = account.getPasswordResetCaptcha(registerBaseUrl, domainSuffix, symbol)

    suspend fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): String {
        return account.sendPasswordResetRequest(registerBaseUrl, domainSuffix, symbol, email.trim(), captchaCode)
    }

    suspend fun getUserSubjects(): RegisterUser = register.getUserSubjects()

    // AUTHORIZED - student

    suspend fun getCurrentStudent(): RegisterStudent {
        val loginResult = serviceManager.userLogin()
        return when (loginResult.isStudentSchoolUseEduOne) {
            true -> studentPlus.getStudent(studentId)
            else -> studentStart.getStudent(studentId, unitId)
        }
    }

    suspend fun authorizePermission(pesel: String): Boolean = when (isEduOne) {
        true -> studentPlus.authorizePermission(pesel, studentId, diaryId, unitId)
        else -> student.authorizePermission(pesel)
    }

    suspend fun getSemesters(): List<Semester> = when (isEduOne) {
        true -> studentPlus.getSemesters(studentId)
        else -> studentStart.getSemesters()
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null): List<Attendance> {
        if (diaryId == 0) return emptyList()

        return when (isEduOne) {
            true -> studentPlus.getAttendance(startDate, endDate, studentId, diaryId, unitId)
            else -> student.getAttendance(startDate, endDate)
        }
    }

    suspend fun getAttendanceSummary(subjectId: Int? = -1): List<AttendanceSummary> {
        if (diaryId == 0) return emptyList()

        return when (isEduOne) {
            true -> studentPlus.getAttendanceSummary(studentId, diaryId, unitId)
            else -> student.getAttendanceSummary(subjectId)
        }
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null): Boolean {
        return when (isEduOne) {
            true -> studentPlus.excuseForAbsence(absents, content, studentId, diaryId, unitId)
            else -> student.excuseForAbsence(absents, content)
        }
    }

    suspend fun getSubjects(): List<Subject> = when (isEduOne) {
        true -> listOf(Subject())
        else -> student.getSubjects()
    }

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate? = null): List<Exam> {
        if (diaryId == 0) return emptyList()
        return when (isEduOne) {
            true -> studentPlus.getExams(startDate, endDate, studentId, diaryId, unitId)
            else -> student.getExams(startDate, endDate)
        }
    }

    suspend fun getGrades(semester: Int): Grades {
        if (diaryId == 0) {
            return Grades(
                details = emptyList(),
                summary = emptyList(),
                descriptive = emptyList(),
                isAverage = false,
                isPoints = false,
                isForAdults = false,
                type = -1,
            )
        }
        return when (isEduOne) {
            true -> studentPlus.getGrades(semester, studentId, diaryId, unitId)
            else -> student.getGrades(semester)
        }
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradesStatisticsPartial> {
        if (diaryId == 0) return emptyList()

        return when (isEduOne) {
            true -> throw FeatureUnavailableException()
            else -> student.getGradesPartialStatistics(semesterId)
        }
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsSummary> {
        if (diaryId == 0) return emptyList()

        return when (isEduOne) {
            true -> throw FeatureUnavailableException()
            else -> student.getGradesPointsStatistics(semesterId)
        }
    }

    suspend fun getGradesSemesterStatistics(semesterId: Int): List<GradesStatisticsSemester> {
        if (diaryId == 0) return emptyList()

        return when (isEduOne) {
            true -> throw FeatureUnavailableException()
            else -> student.getGradesAnnualStatistics(semesterId)
        }
    }

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): List<Homework> {
        if (diaryId == 0) return emptyList()

        return when (isEduOne) {
            true -> studentPlus.getHomework(startDate, endDate, studentId, diaryId, unitId)
            else -> student.getHomework(startDate, endDate)
        }
    }

    suspend fun getNotes(): List<Note> = when (isEduOne) {
        true -> studentPlus.getNotes(studentId, diaryId, unitId)
        else -> student.getNotes()
    }

    suspend fun getConferences(): List<Conference> = when (isEduOne) {
        true -> studentPlus.getConferences(studentId, diaryId, unitId)
        else -> student.getConferences()
    }

    suspend fun getMenu(date: LocalDate): List<Menu> = when (isEduOne) {
        true -> TODO()
        else -> student.getMenu(date)
    }

    suspend fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Timetable {
        if (diaryId == 0) {
            return Timetable(
                headers = emptyList(),
                lessons = emptyList(),
                additional = emptyList(),
            )
        }

        return when (isEduOne) {
            true -> studentPlus.getTimetable(startDate, endDate, studentId, diaryId, unitId)
            else -> student.getTimetable(startDate, endDate)
        }
    }

    suspend fun getCompletedLessons(startDate: LocalDate, endDate: LocalDate? = null, subjectId: Int = -1): List<CompletedLesson> {
        if (diaryId == 0) return emptyList()

        return when (isEduOne) {
            true -> studentPlus.getCompletedLessons(startDate, endDate, studentId, diaryId, unitId)
            else -> student.getCompletedLessons(startDate, endDate, subjectId)
        }
    }

    suspend fun getRegisteredDevices(): List<Device> = when (isEduOne) {
        true -> studentPlus.getRegisteredDevices(studentId, diaryId, unitId)
        else -> student.getRegisteredDevices()
    }

    suspend fun getToken(): TokenResponse {
        return when (isEduOne) {
            true -> studentPlus.getToken(studentId, diaryId, unitId)
            else -> student.getToken()
        }
    }

    suspend fun unregisterDevice(id: Int): Boolean = when (isEduOne) {
        true -> TODO()
        else -> student.unregisterDevice(id)
    }

    suspend fun getTeachers(): List<Teacher> = when (isEduOne) {
        true -> studentPlus.getTeachers(studentId, diaryId, unitId)
        else -> student.getTeachers()
    }

    suspend fun getSchool(): School = when (isEduOne) {
        true -> studentPlus.getSchool(studentId, diaryId, unitId)
        else -> student.getSchool()
    }

    suspend fun getStudentInfo(): StudentInfo = when (isEduOne) {
        true -> studentPlus.getStudentInfo(studentId, diaryId, unitId)
        else -> student.getStudentInfo()
    }

    suspend fun getStudentPhoto(): StudentPhoto = when (isEduOne) {
        true -> studentPlus.getStudentPhoto(studentId, diaryId, unitId)
        else -> student.getStudentPhoto()
    }

    // MESSAGES

    suspend fun getMailboxes(): List<Mailbox> = messages.getMailboxes()

    suspend fun getRecipients(mailboxKey: String): List<Recipient> = messages.getRecipients(mailboxKey)

    suspend fun getMessages(
        folder: Folder,
        mailboxKey: String? = null,
        lastMessageKey: Int = 0,
        pageSize: Int = 50,
    ): List<MessageMeta> = when (folder) {
        Folder.RECEIVED -> messages.getReceivedMessages(mailboxKey, lastMessageKey, pageSize)
        Folder.SENT -> messages.getSentMessages(mailboxKey, lastMessageKey, pageSize)
        Folder.TRASHED -> messages.getDeletedMessages(mailboxKey, lastMessageKey, pageSize)
    }

    suspend fun getReceivedMessages(mailboxKey: String? = null, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> =
        messages.getReceivedMessages(mailboxKey, lastMessageKey, pageSize)

    suspend fun getSentMessages(mailboxKey: String? = null, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> =
        messages.getSentMessages(mailboxKey, lastMessageKey, pageSize)

    suspend fun getDeletedMessages(mailboxKey: String? = null, lastMessageKey: Int = 0, pageSize: Int = 50): List<MessageMeta> =
        messages.getDeletedMessages(mailboxKey, lastMessageKey, pageSize)

    suspend fun getMessageReplayDetails(globalKey: String): MessageReplayDetails = messages.getMessageReplayDetails(globalKey)

    suspend fun getMessageDetails(globalKey: String, markAsRead: Boolean): MessageDetails = messages.getMessageDetails(globalKey, markAsRead)

    suspend fun markMessageRead(globalKey: String) = messages.markMessageRead(globalKey)

    suspend fun sendMessage(subject: String, content: String, recipients: List<String>, senderMailboxId: String) =
        messages.sendMessage(subject, content, recipients, senderMailboxId)

    suspend fun restoreMessages(messagesToRestore: List<String>) = messages.restoreFromTrash(messagesToRestore)

    suspend fun deleteMessages(messagesToDelete: List<String>, removeForever: Boolean) = messages.deleteMessages(messagesToDelete, removeForever)

    // Homepage

    suspend fun getDirectorInformation(): List<DirectorInformation> = homepage.getDirectorInformation()

    suspend fun getLastAnnouncements(): List<LastAnnouncement> = homepage.getLastAnnouncements()

    suspend fun getSelfGovernments(): List<GovernmentUnit> = homepage.getSelfGovernments()

    suspend fun getStudentThreats(): List<String> = homepage.getStudentThreats()

    suspend fun getStudentsTrips(): List<String> = homepage.getStudentsTrips()

    suspend fun getLastGrades(): List<String> = homepage.getLastGrades()

    suspend fun getFreeDays(): List<String> = homepage.getFreeDays()

    suspend fun getKidsLuckyNumbers(): List<LuckyNumber> = homepage.getKidsLuckyNumbers()

    suspend fun getKidsLessonPlan(): List<String> = homepage.getKidsLessonPlan()

    suspend fun getLastHomework(): List<String> = homepage.getLastHomework()

    suspend fun getLastTests(): List<String> = homepage.getLastTests()

    suspend fun getLastStudentLessons(): List<String> = homepage.getLastStudentLessons()
}
