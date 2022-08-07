package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.attendance.Absent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.Subject
import io.github.wulkanowy.sdk.scrapper.conferences.Conference
import io.github.wulkanowy.sdk.scrapper.exams.Exam
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.grades.Grade
import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradesFull
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsPartial
import io.github.wulkanowy.sdk.scrapper.grades.GradesStatisticsSemester
import io.github.wulkanowy.sdk.scrapper.home.DirectorInformation
import io.github.wulkanowy.sdk.scrapper.home.GovernmentUnit
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.homework.Homework
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.messages.Folder
import io.github.wulkanowy.sdk.scrapper.messages.Message
import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.ReportingUnit
import io.github.wulkanowy.sdk.scrapper.messages.SentMessage
import io.github.wulkanowy.sdk.scrapper.mobile.Device
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.scrapper.notes.Note
import io.github.wulkanowy.sdk.scrapper.register.Semester
import io.github.wulkanowy.sdk.scrapper.register.Student
import io.github.wulkanowy.sdk.scrapper.repository.AccountRepository
import io.github.wulkanowy.sdk.scrapper.repository.HomepageRepository
import io.github.wulkanowy.sdk.scrapper.repository.MessagesRepository
import io.github.wulkanowy.sdk.scrapper.repository.RegisterRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentRepository
import io.github.wulkanowy.sdk.scrapper.repository.StudentStartRepository
import io.github.wulkanowy.sdk.scrapper.school.School
import io.github.wulkanowy.sdk.scrapper.school.Teacher
import io.github.wulkanowy.sdk.scrapper.service.ServiceManager
import io.github.wulkanowy.sdk.scrapper.student.StudentInfo
import io.github.wulkanowy.sdk.scrapper.student.StudentPhoto
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableAdditional
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableDayHeader
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableFull
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

    /**
     * @see <a href="https://deviceatlas.com/blog/most-popular-android-smartphones#poland">The most popular Android phones - 2019</a>
     * @see <a href="http://www.tera-wurfl.com/explore/?action=wurfl_id&id=samsung_sm_g950f_int_ver1">Tera-WURFL Explorer - Samsung SM-G950F (Galaxy S8)</a>
     */
    var androidVersion: String = "7.0"
        set(value) {
            if (field != value) changeManager.reset()
            field = value
        }

    var buildTag: String = "SM-G950F Build/NRD90M"
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
            kindergartenDiaryId = kindergartenDiaryId,
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
            startSymbol = normalizedSymbol,
            email = email,
            password = password,
            loginHelper = LoginHelper(
                loginType = loginType,
                schema = schema,
                host = host,
                symbol = normalizedSymbol,
                cookies = serviceManager.getCookieManager(),
                api = serviceManager.getLoginService()
            ),
            register = serviceManager.getRegisterService(),
            messages = serviceManager.getMessagesService(withLogin = false),
            student = serviceManager.getStudentService(withLogin = false, studentInterceptor = false),
            url = serviceManager.urlGenerator
        )
    }

    private val studentStart by resettableLazy(changeManager) {
        if (0 == studentId) throw ScrapperException("Student id is not set")
        if (0 == classId && 0 == kindergartenDiaryId) throw ScrapperException("Class id is not set")
        StudentStartRepository(
            studentId = studentId,
            classId = classId,
            unitId = unitId,
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

    suspend fun getPasswordResetCaptcha(registerBaseUrl: String, symbol: String): Pair<String, String> = account.getPasswordResetCaptcha(registerBaseUrl, symbol)

    suspend fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): String {
        return account.sendPasswordResetRequest(registerBaseUrl, symbol, email.trim(), captchaCode)
    }

    suspend fun getStudents(): List<Student> = register.getStudents()

    suspend fun getSemesters(): List<Semester> = studentStart.getSemesters()

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null): List<Attendance> = student.getAttendance(startDate, endDate)

    suspend fun getAttendanceSummary(subjectId: Int? = -1): List<AttendanceSummary> = student.getAttendanceSummary(subjectId)

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null): Boolean = student.excuseForAbsence(absents, content)

    suspend fun getSubjects(): List<Subject> = student.getSubjects()

    suspend fun getExams(startDate: LocalDate, endDate: LocalDate? = null): List<Exam> = student.getExams(startDate, endDate)

    suspend fun getGradesFull(semester: Int): GradesFull = student.getGradesFull(semester)

    suspend fun getGrades(semesterId: Int): Pair<List<Grade>, List<GradeSummary>> = student.getGrades(semesterId)

    suspend fun getGradesDetails(semesterId: Int? = null): List<Grade> = student.getGradesDetails(semesterId)

    suspend fun getGradesSummary(semesterId: Int? = null): List<GradeSummary> = student.getGradesSummary(semesterId)

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradesStatisticsPartial> = student.getGradesPartialStatistics(semesterId)

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsSummary> = student.getGradesPointsStatistics(semesterId)

    suspend fun getGradesSemesterStatistics(semesterId: Int): List<GradesStatisticsSemester> = student.getGradesAnnualStatistics(semesterId)

    suspend fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): List<Homework> = student.getHomework(startDate, endDate)

    suspend fun getNotes(): List<Note> = student.getNotes()

    suspend fun getConferences(): List<Conference> = student.getConferences()

    suspend fun getTimetableFull(startDate: LocalDate, endDate: LocalDate? = null): TimetableFull = student.getTimetableFull(startDate, endDate)

    suspend fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Pair<List<Timetable>, List<TimetableAdditional>> = student.getTimetable(startDate, endDate)

    suspend fun getTimetableNormal(startDate: LocalDate, endDate: LocalDate? = null): List<Timetable> = student.getTimetableNormal(startDate, endDate)

    suspend fun getTimetableHeaders(startDate: LocalDate): List<TimetableDayHeader> = student.getTimetableHeaders(startDate)

    suspend fun getTimetableAdditional(startDate: LocalDate): List<TimetableAdditional> = student.getTimetableAdditional(startDate)

    suspend fun getCompletedLessons(startDate: LocalDate, endDate: LocalDate? = null, subjectId: Int = -1): List<CompletedLesson> = student.getCompletedLessons(startDate, endDate, subjectId)

    suspend fun getRegisteredDevices(): List<Device> = student.getRegisteredDevices()

    suspend fun getToken(): TokenResponse = student.getToken()

    suspend fun unregisterDevice(id: Int): Boolean = student.unregisterDevice(id)

    suspend fun getTeachers(): List<Teacher> = student.getTeachers()

    suspend fun getSchool(): School = student.getSchool()

    suspend fun getStudentInfo(): StudentInfo = student.getStudentInfo()

    suspend fun getStudentPhoto(): StudentPhoto = student.getStudentPhoto()

    suspend fun getReportingUnits(): List<ReportingUnit> = messages.getReportingUnits()

    suspend fun getRecipients(unitId: Int, role: Int = 2): List<Recipient> = messages.getRecipients(unitId, role)

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

    suspend fun getSentMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null): List<Message> = messages.getSentMessages(startDate, endDate)

    suspend fun getDeletedMessages(startDate: LocalDateTime? = null, endDate: LocalDateTime? = null): List<Message> = messages.getDeletedMessages(startDate, endDate)

    suspend fun getMessageRecipients(messageId: Int, loginId: Int = 0): List<Recipient> = messages.getMessageRecipients(messageId, loginId)

    suspend fun getMessageDetails(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null): Message = messages.getMessageDetails(messageId, folderId, read, id)

    suspend fun getMessageContent(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null): String = messages.getMessage(messageId, folderId, read, id)

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>): SentMessage = messages.sendMessage(subject, content, recipients)

    suspend fun deleteMessages(messagesToDelete: List<Int>, folderId: Int): Boolean = messages.deleteMessages(messagesToDelete, folderId)

    suspend fun getDirectorInformation(): List<DirectorInformation> = homepage.getDirectorInformation()

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
