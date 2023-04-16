package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.hebe.Hebe
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
import io.github.wulkanowy.sdk.mapper.mapHebeUser
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
import io.github.wulkanowy.sdk.mapper.mapSubjects
import io.github.wulkanowy.sdk.mapper.mapTeachers
import io.github.wulkanowy.sdk.mapper.mapTimetableFull
import io.github.wulkanowy.sdk.mapper.mapToScrapperAbsent
import io.github.wulkanowy.sdk.mapper.mapToUnits
import io.github.wulkanowy.sdk.mapper.mapToken
import io.github.wulkanowy.sdk.mapper.mapUser
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
import io.github.wulkanowy.sdk.pojo.RegisterStudent
import io.github.wulkanowy.sdk.pojo.RegisterUser
import io.github.wulkanowy.sdk.pojo.School
import io.github.wulkanowy.sdk.pojo.Semester
import io.github.wulkanowy.sdk.pojo.StudentInfo
import io.github.wulkanowy.sdk.pojo.StudentPhoto
import io.github.wulkanowy.sdk.pojo.Subject
import io.github.wulkanowy.sdk.pojo.Teacher
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.pojo.Token
import io.github.wulkanowy.sdk.scrapper.Scrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.ZoneId

class Sdk {

    enum class Mode {
        HEBE,
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

    private val hebe = Hebe()

    private val registerTimeZone = ZoneId.of("Europe/Warsaw")

    var mode = Mode.HYBRID

    var mobileBaseUrl = ""
        set(value) {
            field = value
            hebe.baseUrl = value
        }

    var keyId = ""
        set(value) {
            field = value
            hebe.keyId = value
        }

    var privatePem = ""
        set(value) {
            field = value
            hebe.privatePem = privatePem
        }

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
            scrapper.schoolId = value
            hebe.schoolId = value
        }

    var classId = 0
        set(value) {
            field = value
            scrapper.classId = value
            // hebe.classId = value // todo
        }

    var studentId = 0
        set(value) {
            field = value
            scrapper.studentId = value
            // hebe.studentId = value // todo
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
            hebe.deviceModel = value
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
        hebe.addInterceptor(interceptor, network)
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

    suspend fun getUserSubjectsFromScrapper(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        symbol: String = "Default",
    ): RegisterUser = withContext(Dispatchers.IO) {
        scrapper.let {
            it.baseUrl = scrapperBaseUrl
            it.email = email
            it.password = password
            it.symbol = symbol
            it.getUserSubjects().mapUser()
        }
    }

    suspend fun getStudentsFromHebe(
        token: String,
        pin: String,
        symbol: String,
        firebaseToken: String? = null,
    ): RegisterUser {
        val registerDevice = hebe.register(
            firebaseToken = firebaseToken,
            token = token,
            pin = pin,
            symbol = symbol,
        )
        return hebe
            .getStudents(registerDevice.restUrl)
            .mapHebeUser(registerDevice)
    }

    suspend fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        startSymbol: String = "Default",
        firebaseToken: String? = null,
    ): RegisterUser = withContext(Dispatchers.IO) {
        val scrapperUser = getUserSubjectsFromScrapper(email, password, scrapperBaseUrl, startSymbol)
        scrapperUser.copy(
            loginMode = Mode.HYBRID,
            symbols = scrapperUser.symbols
                .mapNotNull { symbol ->
                    val school = symbol.schools.firstOrNull {
                        it.subjects.filterIsInstance<RegisterStudent>().isNotEmpty()
                    } ?: return@mapNotNull null
                    val student = school.subjects
                        .firstOrNull() as? RegisterStudent ?: return@mapNotNull null
                    scrapper.also {
                        it.symbol = symbol.symbol
                        it.schoolId = school.schoolId
                        it.studentId = student.studentId
                        it.diaryId = -1
                        it.classId = student.classId
                        it.loginType = Scrapper.LoginType.valueOf(scrapperUser.loginType!!.name)
                    }
                    val token = scrapper.getToken()
                    val hebeUser = getStudentsFromHebe(
                        token = token.token,
                        pin = token.pin,
                        symbol = token.symbol,
                        firebaseToken = firebaseToken,
                    )
                    symbol.copy(
                        keyId = hebeUser.symbols.first().keyId,
                        privatePem = hebeUser.symbols.first().privatePem,
                        hebeBaseUrl = hebeUser.symbols.first().hebeBaseUrl,
                    )
                },
        )
    }

    suspend fun getSemesters(): List<Semester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().mapSemesters()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate): List<Attendance> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).mapAttendance()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getAttendanceSummary(subjectId: Int? = -1): List<AttendanceSummary> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getAttendanceSummary(subjectId).mapAttendanceSummary()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.excuseForAbsence(absents.mapToScrapperAbsent(), content)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getSubjects(): List<Subject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSubjects().mapSubjects()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getExams(start: LocalDate, end: LocalDate): List<Exam> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getExams(start, end).mapExams()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getGrades(semesterId: Int): Grades = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).mapGrades()
            Mode.HYBRID, Mode.HEBE -> hebe.getGrades(semesterId).mapGrades()
        }
    }

    suspend fun getGradesSemesterStatistics(semesterId: Int): List<GradeStatisticsSemester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesSemesterStatistics(semesterId).mapGradesSemesterStatistics()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradeStatisticsSubject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPartialStatistics(semesterId).mapGradeStatistics()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsStatistics> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPointsStatistics(semesterId).mapGradePointsStatistics()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getHomework(start: LocalDate, end: LocalDate): List<Homework> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getHomework(start, end).mapHomework()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getNotes(): List<Note> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getNotes().mapNotes()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getConferences(): List<Conference> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getConferences().mapConferences(registerTimeZone)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getRegisteredDevices(): List<Device> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRegisteredDevices().mapDevices(registerTimeZone)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getToken(): Token = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getToken().mapToken()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun unregisterDevice(id: Int): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.unregisterDevice(id)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getTeachers(): List<Teacher> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getTeachers().mapTeachers()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getSchool(): School = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSchool().mapSchool()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentInfo(): StudentInfo = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentInfo().mapStudent()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentPhoto(): StudentPhoto = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentPhoto().mapPhoto()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getMailboxes(): List<Mailbox> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMailboxes().mapMailboxes()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getRecipients(mailboxKey: String): List<Recipient> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRecipients(mailboxKey).mapRecipients()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
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
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getSentMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSentMessages(mailboxKey).mapMessages(registerTimeZone, Folder.SENT)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getDeletedMessages(mailboxKey: String? = null): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getDeletedMessages(mailboxKey).mapMessages(registerTimeZone, Folder.TRASHED)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getMessageReplayDetails(messageKey: String): MessageReplayDetails = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageReplayDetails(messageKey).mapScrapperMessage()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getMessageDetails(messageKey: String, markAsRead: Boolean = true): MessageDetails = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageDetails(messageKey, markAsRead).mapScrapperMessage()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>, mailboxId: String) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.sendMessage(subject, content, recipients.map { it.mailboxGlobalKey }, mailboxId)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun deleteMessages(messages: List<String>, removeForever: Boolean = false) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.deleteMessages(messages, removeForever)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getTimetable(start: LocalDate, end: LocalDate): Timetable = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getTimetable(start, end).mapTimetableFull(registerTimeZone)
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1): List<CompletedLesson> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getCompletedLessons(start, end, subjectId).mapCompletedLessons()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
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
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getSelfGovernments(): List<GovernmentUnit> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSelfGovernments().mapToUnits()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentThreats(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentThreats()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getStudentsTrips(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentsTrips()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastGrades(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastGrades()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getFreeDays(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getFreeDays()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getKidsLuckyNumbers(): List<LuckyNumber> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLuckyNumbers().mapLuckyNumbers()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getKidsTimetable(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLessonPlan()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastHomework(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastHomework()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastExams(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastTests()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }

    suspend fun getLastStudentLessons(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastStudentLessons()
            Mode.HEBE -> throw NotImplementedError("Not available in HEBE mode")
        }
    }
}
