package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.mapper.mapAttendance
import io.github.wulkanowy.sdk.mapper.mapAttendanceSummary
import io.github.wulkanowy.sdk.mapper.mapCompletedLessons
import io.github.wulkanowy.sdk.mapper.mapConferences
import io.github.wulkanowy.sdk.mapper.mapDevices
import io.github.wulkanowy.sdk.mapper.mapDirectorInformation
import io.github.wulkanowy.sdk.mapper.mapExams
import io.github.wulkanowy.sdk.mapper.mapGradePointsStatistics
import io.github.wulkanowy.sdk.mapper.mapGradeStatistics
import io.github.wulkanowy.sdk.mapper.mapGrades
import io.github.wulkanowy.sdk.mapper.mapGradesSemesterStatistics
import io.github.wulkanowy.sdk.mapper.mapHomework
import io.github.wulkanowy.sdk.mapper.mapLuckyNumbers
import io.github.wulkanowy.sdk.mapper.mapMailboxes
import io.github.wulkanowy.sdk.mapper.mapMessages
import io.github.wulkanowy.sdk.mapper.mapNotes
import io.github.wulkanowy.sdk.mapper.mapPhoto
import io.github.wulkanowy.sdk.mapper.mapRecipients
import io.github.wulkanowy.sdk.mapper.mapSchool
import io.github.wulkanowy.sdk.mapper.mapScrapperMessage
import io.github.wulkanowy.sdk.mapper.mapSemesters
import io.github.wulkanowy.sdk.mapper.mapStudent
import io.github.wulkanowy.sdk.mapper.mapStudents
import io.github.wulkanowy.sdk.mapper.mapSubjects
import io.github.wulkanowy.sdk.mapper.mapTeachers
import io.github.wulkanowy.sdk.mapper.mapTimetableFull
import io.github.wulkanowy.sdk.mapper.mapToScrapperAbsent
import io.github.wulkanowy.sdk.mapper.mapToUnits
import io.github.wulkanowy.sdk.mapper.mapToken
import io.github.wulkanowy.sdk.pojo.Absent
import io.github.wulkanowy.sdk.pojo.Attendance
import io.github.wulkanowy.sdk.pojo.AttendanceSummary
import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.github.wulkanowy.sdk.pojo.Conference
import io.github.wulkanowy.sdk.pojo.Device
import io.github.wulkanowy.sdk.pojo.DirectorInformation
import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.GovernmentUnit
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemester
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.sdk.pojo.Grades
import io.github.wulkanowy.sdk.pojo.Homework
import io.github.wulkanowy.sdk.pojo.LuckyNumber
import io.github.wulkanowy.sdk.pojo.Mailbox
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.MessageReplayDetails
import io.github.wulkanowy.sdk.pojo.Note
import io.github.wulkanowy.sdk.pojo.Recipient
import io.github.wulkanowy.sdk.pojo.School
import io.github.wulkanowy.sdk.pojo.Semester
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.sdk.pojo.StudentInfo
import io.github.wulkanowy.sdk.pojo.StudentPhoto
import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.pojo.Teacher
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.pojo.Token
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.register.RegisterUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.ZoneId

class Sdk {

    enum class Mode {
        SCRAPPER,
        HYBRID,
    }

    enum class ScrapperLoginType {
        AUTO,
        STANDARD,
        ADFS,
        ADFSCards,
        ADFSLight,
        ADFSLightScoped,
        ADFSLightCufs,
    }

    private val scrapper = Scrapper()

    private val registerTimeZone = ZoneId.of("Europe/Warsaw")

    var mode = Mode.HYBRID

    var scrapperBaseUrl = ""
        set(value) {
            field = value
            scrapper.baseUrl = value
        }

    var email = ""
        set(value) {
            field = value
            scrapper.email = value
        }

    var password = ""
        set(value) {
            field = value
            scrapper.password = value
        }

    var schoolSymbol = ""
        set(value) {
            field = value
            scrapper.schoolSymbol = value
        }

    var classId = 0
        set(value) {
            field = value
            scrapper.classId = value
        }

    var studentId = 0
        set(value) {
            field = value
            scrapper.studentId = value
        }

    var diaryId = 0
        set(value) {
            field = value
            scrapper.diaryId = value
        }

    var kindergartenDiaryId = 0
        set(value) {
            field = value
            scrapper.kindergartenDiaryId = value
        }

    var schoolYear = 0
        set(value) {
            field = value
            scrapper.schoolYear = value
        }

    var symbol = ""
        set(value) {
            field = value
            scrapper.symbol = value
        }

    var loginType = ScrapperLoginType.AUTO
        set(value) {
            field = value
            scrapper.loginType = Scrapper.LoginType.valueOf(value.name)
        }

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
            scrapper.logLevel = value
        }

    var userAgentTemplate = ""
        set(value) {
            field = value
            scrapper.userAgentTemplate = value
        }

    var androidVersion = "7.0"
        set(value) {
            field = value
            scrapper.androidVersion = value
        }

    var buildTag = "SM-G950F Build/NRD90M"
        set(value) {
            field = value
            scrapper.buildTag = value
        }

    var emptyCookieJarInterceptor: Boolean = false
        set(value) {
            field = value
            scrapper.emptyCookieJarInterceptor = value
        }

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun setSimpleHttpLogger(logger: (String) -> Unit) {
        logLevel = HttpLoggingInterceptor.Level.NONE
        val interceptor = HttpLoggingInterceptor {
            logger(it)
        }.setLevel(HttpLoggingInterceptor.Level.BASIC)
        addInterceptor(interceptor)
    }

    fun addInterceptor(interceptor: Interceptor, network: Boolean = false) {
        scrapper.addInterceptor(interceptor, network)
        interceptors.add(interceptor to network)
    }

    fun switchDiary(diaryId: Int, kindergartenDiaryId: Int, schoolYear: Int): Sdk {
        return also {
            it.diaryId = diaryId
            it.kindergartenDiaryId = kindergartenDiaryId
            it.schoolYear = schoolYear
        }
    }

    suspend fun getPasswordResetCaptchaCode(registerBaseUrl: String, symbol: String) = withContext(Dispatchers.IO) {
        scrapper.getPasswordResetCaptcha(registerBaseUrl, symbol)
    }

    suspend fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String) = withContext(Dispatchers.IO) {
        scrapper.sendPasswordResetRequest(registerBaseUrl, symbol, email, captchaCode)
    }

    suspend fun getStudentsFromScrapper(email: String, password: String, scrapperBaseUrl: String, symbol: String = "Default"): List<Student> = withContext(Dispatchers.IO) {
        scrapper.let {
            it.baseUrl = scrapperBaseUrl
            it.email = email
            it.password = password
            it.symbol = symbol
            it.getStudents().mapStudents()
        }
    }

    suspend fun getUserSubjectsFromScrapper(email: String, password: String, scrapperBaseUrl: String, symbol: String = "Default"): RegisterUser = withContext(Dispatchers.IO) {
        scrapper.let {
            it.baseUrl = scrapperBaseUrl
            it.email = email
            it.password = password
            it.symbol = symbol
            it.getUserSubjects()
        }
    }

    suspend fun getSemesters(): List<Semester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().mapSemesters()
        }
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate): List<Attendance> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).mapAttendance()
        }
    }

    suspend fun getAttendanceSummary(subjectId: Int? = -1): List<AttendanceSummary> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getAttendanceSummary(subjectId).mapAttendanceSummary()
        }
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.excuseForAbsence(absents.mapToScrapperAbsent(), content)
        }
    }

    suspend fun getSubjects(): List<Subject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSubjects().mapSubjects()
        }
    }

    suspend fun getExams(start: LocalDate, end: LocalDate): List<Exam> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getExams(start, end).mapExams()
        }
    }

    suspend fun getGrades(semesterId: Int): Grades = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGrades(semesterId).mapGrades()
        }
    }

    suspend fun getGradesSemesterStatistics(semesterId: Int): List<GradeStatisticsSemester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesSemesterStatistics(semesterId).mapGradesSemesterStatistics()
        }
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradeStatisticsSubject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPartialStatistics(semesterId).mapGradeStatistics()
        }
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsStatistics> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPointsStatistics(semesterId).mapGradePointsStatistics()
        }
    }

    suspend fun getHomework(start: LocalDate, end: LocalDate): List<Homework> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getHomework(start, end).mapHomework()
        }
    }

    suspend fun getNotes(): List<Note> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getNotes().mapNotes()
        }
    }

    suspend fun getConferences(): List<Conference> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getConferences().mapConferences(registerTimeZone)
        }
    }

    suspend fun getRegisteredDevices(): List<Device> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRegisteredDevices().mapDevices(registerTimeZone)
        }
    }

    suspend fun getToken(): Token = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getToken().mapToken()
        }
    }

    suspend fun unregisterDevice(id: Int): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.unregisterDevice(id)
        }
    }

    suspend fun getTeachers(): List<Teacher> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getTeachers().mapTeachers()
        }
    }

    suspend fun getSchool(): School = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSchool().mapSchool()
        }
    }

    suspend fun getStudentInfo(): StudentInfo = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentInfo().mapStudent()
        }
    }

    suspend fun getStudentPhoto(): StudentPhoto = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentPhoto().mapPhoto()
        }
    }

    suspend fun getMailboxes(): List<Mailbox> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMailboxes().mapMailboxes()
        }
    }

    suspend fun getRecipients(mailboxKey: String): List<Recipient> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRecipients(mailboxKey).mapRecipients()
        }
    }

    suspend fun getMessages(folder: Folder, mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (folder) {
            Folder.RECEIVED -> getReceivedMessages(mailboxKey)
            Folder.SENT -> getSentMessages(mailboxKey)
            Folder.TRASHED -> getDeletedMessages(mailboxKey)
        }
    }

    suspend fun getReceivedMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getReceivedMessages(mailboxKey).mapMessages(registerTimeZone, Folder.RECEIVED)
        }
    }

    suspend fun getSentMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSentMessages(mailboxKey).mapMessages(registerTimeZone, Folder.SENT)
        }
    }

    suspend fun getDeletedMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getDeletedMessages(mailboxKey).mapMessages(registerTimeZone, Folder.TRASHED)
        }
    }

    suspend fun getMessageReplayDetails(messageKey: String): MessageReplayDetails = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageReplayDetails(messageKey).mapScrapperMessage()
        }
    }

    suspend fun getMessageDetails(messageKey: String, markAsRead: Boolean = true): MessageDetails = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageDetails(messageKey, markAsRead).mapScrapperMessage()
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>, mailboxId: String) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.sendMessage(subject, content, recipients.map { it.mailboxGlobalKey }, mailboxId)
        }
    }

    suspend fun deleteMessages(messages: List<String>, removeForever: Boolean = false) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.deleteMessages(messages, removeForever)
        }
    }

    suspend fun getTimetable(start: LocalDate, end: LocalDate): Timetable = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getTimetable(start, end).mapTimetableFull(registerTimeZone)
        }
    }

    suspend fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1): List<CompletedLesson> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getCompletedLessons(start, end, subjectId).mapCompletedLessons()
        }
    }

    suspend fun getLuckyNumber(unitName: String = ""): LuckyNumber? = withContext(Dispatchers.IO) {
        val numbers = getKidsLuckyNumbers()

        // if lucky number unitName match unit name from student tile
        numbers.singleOrNull { number -> number.unitName == unitName }?.let {
            return@withContext it
        }

        // if there is only one lucky number and its doesn't match to any student
        if (numbers.size == 1) {
            return@withContext numbers.single()
        }

        // if there is more than one lucky number, return first (just like this was working before 0.16.0)
        if (numbers.size > 1) {
            return@withContext numbers.first()
        }

        // else
        null
    }

    suspend fun getDirectorInformation(): List<DirectorInformation> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getDirectorInformation().mapDirectorInformation()
        }
    }

    suspend fun getSelfGovernments(): List<GovernmentUnit> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSelfGovernments().mapToUnits()
        }
    }

    suspend fun getStudentThreats(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentThreats()
        }
    }

    suspend fun getStudentsTrips(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentsTrips()
        }
    }

    suspend fun getLastGrades(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastGrades()
        }
    }

    suspend fun getFreeDays(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getFreeDays()
        }
    }

    suspend fun getKidsLuckyNumbers(): List<LuckyNumber> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLuckyNumbers().mapLuckyNumbers()
        }
    }

    suspend fun getKidsTimetable(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLessonPlan()
        }
    }

    suspend fun getLastHomework(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastHomework()
        }
    }

    suspend fun getLastExams(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastTests()
        }
    }

    suspend fun getLastStudentLessons(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastStudentLessons()
        }
    }
}
