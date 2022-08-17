package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.mapper.mapAttendance
import io.github.wulkanowy.sdk.mapper.mapAttendanceSummary
import io.github.wulkanowy.sdk.mapper.mapCompletedLessons
import io.github.wulkanowy.sdk.mapper.mapConferences
import io.github.wulkanowy.sdk.mapper.mapDevices
import io.github.wulkanowy.sdk.mapper.mapDirectorInformation
import io.github.wulkanowy.sdk.mapper.mapExams
import io.github.wulkanowy.sdk.mapper.mapFromRecipientsToMobile
import io.github.wulkanowy.sdk.mapper.mapGradePointsStatistics
import io.github.wulkanowy.sdk.mapper.mapGradeStatistics
import io.github.wulkanowy.sdk.mapper.mapGrades
import io.github.wulkanowy.sdk.mapper.mapGradesDetails
import io.github.wulkanowy.sdk.mapper.mapGradesSemesterStatistics
import io.github.wulkanowy.sdk.mapper.mapGradesSummary
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
import io.github.wulkanowy.sdk.mapper.mapTimetable
import io.github.wulkanowy.sdk.mapper.mapTimetableAdditional
import io.github.wulkanowy.sdk.mapper.mapTimetableDayHeaders
import io.github.wulkanowy.sdk.mapper.mapTimetableFull
import io.github.wulkanowy.sdk.mapper.mapToScrapperAbsent
import io.github.wulkanowy.sdk.mapper.mapToUnits
import io.github.wulkanowy.sdk.mapper.mapToken
import io.github.wulkanowy.sdk.mobile.Mobile
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
import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSemester
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.sdk.pojo.GradeSummary
import io.github.wulkanowy.sdk.pojo.GradesFull
import io.github.wulkanowy.sdk.pojo.Homework
import io.github.wulkanowy.sdk.pojo.LuckyNumber
import io.github.wulkanowy.sdk.pojo.Mailbox
import io.github.wulkanowy.sdk.pojo.Message
import io.github.wulkanowy.sdk.pojo.MessageDetails
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
import io.github.wulkanowy.sdk.pojo.TimetableAdditional
import io.github.wulkanowy.sdk.pojo.TimetableDayHeader
import io.github.wulkanowy.sdk.pojo.TimetableFull
import io.github.wulkanowy.sdk.pojo.Token
import io.github.wulkanowy.sdk.scrapper.Scrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class Sdk {

    enum class Mode {
        API,
        SCRAPPER,
        HYBRID
    }

    enum class ScrapperLoginType {
        AUTO,
        STANDARD,
        ADFS,
        ADFSCards,
        ADFSLight,
        ADFSLightScoped,
        ADFSLightCufs
    }

    private val mobile = Mobile()

    private val scrapper = Scrapper()

    private val registerTimeZone = ZoneId.of("Europe/Warsaw")

    var mode = Mode.HYBRID

    var mobileBaseUrl = ""
        set(value) {
            field = value
            mobile.baseUrl = value
        }

    var certKey = ""
        set(value) {
            field = value
            mobile.certKey = value
        }

    var privateKey = ""
        set(value) {
            field = value
            mobile.privateKey = privateKey
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
            scrapper.schoolSymbol = value
            mobile.schoolSymbol = value
        }

    var classId = 0
        set(value) {
            field = value
            scrapper.classId = value
            mobile.classId = value
        }

    var studentId = 0
        set(value) {
            field = value
            scrapper.studentId = value
            mobile.studentId = value
        }

    var loginId = 0
        set(value) {
            field = value
            mobile.loginId = value
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
            mobile.logLevel = value
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
        mobile.setInterceptor(interceptor, network)
        interceptors.add(interceptor to network)
    }

    @Deprecated("use switchDiary(int, int, int) instead", ReplaceWith("switchDiary(diaryId, 0, schoolYear)"))
    fun switchDiary(diaryId: Int, schoolYear: Int): Sdk {
        return switchDiary(diaryId, 0, schoolYear)
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

    suspend fun getStudentsFromMobileApi(token: String, pin: String, symbol: String, firebaseToken: String, apiKey: String = ""): List<Student> = withContext(Dispatchers.IO) {
        mobile.getStudents(mobile.getCertificate(token, pin, symbol, buildTag, androidVersion, firebaseToken), apiKey).mapStudents(symbol)
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

    suspend fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        firebaseToken: String,
        startSymbol: String = "Default",
        apiKey: String = ""
    ) = withContext(Dispatchers.IO) {
        getStudentsFromScrapper(email, password, scrapperBaseUrl, startSymbol)
            .distinctBy { it.symbol }
            .map { scrapperStudent ->
                scrapper.let {
                    it.symbol = scrapperStudent.symbol
                    it.schoolSymbol = scrapperStudent.schoolSymbol
                    it.studentId = scrapperStudent.studentId
                    it.diaryId = -1
                    it.classId = scrapperStudent.classId
                    it.loginType = Scrapper.LoginType.valueOf(scrapperStudent.loginType.name)
                }
                val token = scrapper.getToken()
                getStudentsFromMobileApi(token.token, token.pin, token.symbol, firebaseToken, apiKey).map { student ->
                    student.copy(
                        loginMode = Mode.HYBRID,
                        loginType = scrapperStudent.loginType,
                        scrapperBaseUrl = scrapperStudent.scrapperBaseUrl
                    )
                }
            }.toList().flatten()
    }

    suspend fun getSemesters(): List<Semester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().mapSemesters()
            Mode.API -> mobile.getStudents().mapSemesters(studentId)
        }
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate, semesterId: Int): List<Attendance> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).mapAttendance()
            Mode.HYBRID, Mode.API -> mobile.getAttendance(startDate, endDate, semesterId).mapAttendance(mobile.getDictionaries())
        }
    }

    suspend fun getAttendanceSummary(subjectId: Int? = -1): List<AttendanceSummary> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getAttendanceSummary(subjectId).mapAttendanceSummary()
            Mode.API -> throw FeatureNotAvailableException("Attendance summary is not available in API mode")
        }
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.excuseForAbsence(absents.mapToScrapperAbsent(), content)
            Mode.API -> throw FeatureNotAvailableException("Absence excusing is not available in API mode")
        }
    }

    suspend fun getSubjects(): List<Subject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSubjects().mapSubjects()
            Mode.API -> mobile.getDictionaries().subjects.mapSubjects()
        }
    }

    suspend fun getExams(start: LocalDate, end: LocalDate, semesterId: Int): List<Exam> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getExams(start, end).mapExams()
            Mode.HYBRID, Mode.API -> mobile.getExams(start, end, semesterId).mapExams(mobile.getDictionaries())
        }
    }

    suspend fun getGradesFull(semesterId: Int): GradesFull = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getGradesFull(semesterId).mapGrades()
            else -> TODO()
        }
    }

    suspend fun getGrades(semesterId: Int): Pair<List<Grade>, List<GradeSummary>> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).mapGrades()
            Mode.HYBRID, Mode.API -> mobile.getGrades(semesterId).mapGrades(mobile.getDictionaries())
        }
    }

    suspend fun getGradesDetails(semesterId: Int): List<Grade> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getGradesDetails(semesterId).mapGradesDetails()
            Mode.HYBRID, Mode.API -> mobile.getGradesDetails(semesterId).mapGradesDetails(mobile.getDictionaries())
        }
    }

    suspend fun getGradesSummary(semesterId: Int): List<GradeSummary> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getGradesSummary(semesterId).mapGradesSummary()
            Mode.HYBRID, Mode.API -> mobile.getGradesSummary(semesterId).mapGradesSummary(mobile.getDictionaries())
        }
    }

    suspend fun getGradesSemesterStatistics(semesterId: Int): List<GradeStatisticsSemester> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesSemesterStatistics(semesterId).mapGradesSemesterStatistics()
            Mode.API -> throw FeatureNotAvailableException("Class grades annual statistics is not available in API mode")
        }
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradeStatisticsSubject> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPartialStatistics(semesterId).mapGradeStatistics()
            Mode.API -> throw FeatureNotAvailableException("Class grades partial statistics is not available in API mode")
        }
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsStatistics> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPointsStatistics(semesterId).mapGradePointsStatistics()
            Mode.API -> throw FeatureNotAvailableException("Class grades points statistics is not available in API mode")
        }
    }

    suspend fun getHomework(start: LocalDate, end: LocalDate, semesterId: Int = 0): List<Homework> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getHomework(start, end).mapHomework()
            Mode.HYBRID, Mode.API -> mobile.getHomework(start, end, semesterId).mapHomework(mobile.getDictionaries())
        }
    }

    suspend fun getNotes(semesterId: Int): List<Note> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getNotes().mapNotes()
            Mode.HYBRID, Mode.API -> mobile.getNotes(semesterId).mapNotes(mobile.getDictionaries())
        }
    }

    suspend fun getConferences(): List<Conference> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getConferences().mapConferences(registerTimeZone)
            Mode.API -> throw FeatureNotAvailableException("Conferences is not available in API mode")
        }
    }

    suspend fun getRegisteredDevices(): List<Device> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRegisteredDevices().mapDevices(registerTimeZone)
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    suspend fun getToken(): Token = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getToken().mapToken()
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    suspend fun unregisterDevice(id: Int): Boolean = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.unregisterDevice(id)
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    suspend fun getTeachers(semesterId: Int): List<Teacher> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTeachers().mapTeachers()
            Mode.HYBRID, Mode.API -> mobile.getTeachers(studentId, semesterId).mapTeachers(mobile.getDictionaries())
        }
    }

    suspend fun getSchool(): School = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSchool().mapSchool()
            Mode.API -> throw FeatureNotAvailableException("School info is not available in API mode")
        }
    }

    suspend fun getStudentInfo(): StudentInfo = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentInfo().mapStudent()
            Mode.API -> throw FeatureNotAvailableException("Student info is not available in API mode")
        }
    }

    suspend fun getStudentPhoto(): StudentPhoto = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentPhoto().mapPhoto()
            Mode.API -> throw FeatureNotAvailableException("Student photo is not available in API mode")
        }
    }

    suspend fun getMailboxes(): List<Mailbox> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMailboxes().mapMailboxes()
            Mode.API -> TODO()
        }
    }

    suspend fun getRecipients(mailboxKey: String): List<Recipient> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRecipients(mailboxKey).mapRecipients()
            Mode.API -> mobile.getDictionaries().teachers.mapRecipients(-1)
        }
    }

    suspend fun getMessages(folder: Folder): List<Message> = withContext(Dispatchers.IO) {
        when (folder) {
            Folder.RECEIVED -> getReceivedMessages()
            Folder.SENT -> getSentMessages()
            Folder.TRASHED -> getDeletedMessages()
        }
    }

    suspend fun getReceivedMessages(): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getReceivedMessages().mapMessages(registerTimeZone, Folder.RECEIVED)
            Mode.API -> mobile.getMessages(LocalDateTime.now(), LocalDateTime.now()).mapMessages(registerTimeZone)
        }
    }

    suspend fun getSentMessages(): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSentMessages().mapMessages(registerTimeZone, Folder.SENT)
            Mode.API -> mobile.getMessagesSent(LocalDateTime.now(), LocalDateTime.now()).mapMessages(registerTimeZone)
        }
    }

    suspend fun getDeletedMessages(): List<Message> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getDeletedMessages().mapMessages(registerTimeZone, Folder.TRASHED)
            Mode.API -> mobile.getMessagesDeleted(LocalDateTime.now(), LocalDateTime.now()).mapMessages(registerTimeZone)
        }
    }

    suspend fun getMessageRecipients(messageId: String): List<Recipient> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageRecipients(messageId).mapRecipients()
            Mode.API -> TODO()
        }
    }

    suspend fun getMessageDetails(messageId: String): MessageDetails = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageDetails(messageId).mapScrapperMessage()
            Mode.API -> {
                mobile.changeMessageStatus(messageId, "", "Widoczna")
                TODO()
            }
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>, mailboxId: String) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.sendMessage(subject, content, recipients.map { it.mailboxGlobalKey }, mailboxId)
            Mode.API -> mobile.sendMessage(subject, content, recipients.mapFromRecipientsToMobile())
        }
    }

    suspend fun deleteMessages(messages: List<String>) = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.deleteMessages(messages)
            Mode.HYBRID, Mode.API -> messages.map { messageId ->
                mobile.changeMessageStatus(messageId, "", "Usunieta")
            }.let { Unit }
        }
    }

    suspend fun getTimetableFull(start: LocalDate, end: LocalDate): TimetableFull = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetableFull(start, end).mapTimetableFull(registerTimeZone)
            Mode.HYBRID, Mode.API -> mobile.getTimetable(start, end, 0).mapTimetableFull(mobile.getDictionaries(), registerTimeZone)
        }
    }

    suspend fun getTimetable(start: LocalDate, end: LocalDate): Pair<List<Timetable>, List<TimetableAdditional>> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetable(start, end)
                .let { (normal, additional) -> normal.mapTimetable(registerTimeZone) to additional.mapTimetableAdditional(registerTimeZone) }

            Mode.HYBRID, Mode.API -> mobile.getTimetable(start, end, 0).mapTimetable(mobile.getDictionaries(), registerTimeZone) to emptyList()
        }
    }

    suspend fun getTimetableHeaders(start: LocalDate, end: LocalDate): List<TimetableDayHeader> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetableHeaders(start).mapTimetableDayHeaders()
            Mode.HYBRID, Mode.API -> throw FeatureNotAvailableException("Timetable headers are not available in API mode")
        }
    }

    suspend fun getTimetableNormal(start: LocalDate, end: LocalDate): List<Timetable> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetableNormal(start, end).mapTimetable(registerTimeZone)
            Mode.HYBRID, Mode.API -> mobile.getTimetable(start, end, 0).mapTimetable(mobile.getDictionaries(), registerTimeZone)
        }
    }

    suspend fun getTimetableAdditional(start: LocalDate): List<TimetableAdditional> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetableAdditional(start).mapTimetableAdditional(registerTimeZone)
            Mode.HYBRID, Mode.API -> throw FeatureNotAvailableException("Additional timetable lessons are not available in API mode")
        }
    }

    suspend fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1): List<CompletedLesson> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getCompletedLessons(start, end, subjectId).mapCompletedLessons()
            Mode.API -> throw FeatureNotAvailableException("Completed lessons are not available in API mode")
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
            Mode.API -> throw FeatureNotAvailableException("Director information is not available in API mode")
        }
    }

    suspend fun getSelfGovernments(): List<GovernmentUnit> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSelfGovernments().mapToUnits()
            Mode.API -> throw FeatureNotAvailableException("Self governments is not available in API mode")
        }
    }

    suspend fun getStudentThreats(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentThreats()
            Mode.API -> throw FeatureNotAvailableException("Student threats are not available in API mode")
        }
    }

    suspend fun getStudentsTrips(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentsTrips()
            Mode.API -> throw FeatureNotAvailableException("Students trips is not available in API mode")
        }
    }

    suspend fun getLastGrades(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastGrades()
            Mode.API -> throw FeatureNotAvailableException("Last grades is not available in API mode")
        }
    }

    suspend fun getFreeDays(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getFreeDays()
            Mode.API -> throw FeatureNotAvailableException("Free days is not available in API mode")
        }
    }

    suspend fun getKidsLuckyNumbers(): List<LuckyNumber> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLuckyNumbers().mapLuckyNumbers()
            Mode.API -> throw FeatureNotAvailableException("Kids Lucky number is not available in API mode")
        }
    }

    suspend fun getKidsTimetable(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLessonPlan()
            Mode.API -> throw FeatureNotAvailableException("Kids timetable is not available in API mode")
        }
    }

    suspend fun getLastHomework(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastHomework()
            Mode.API -> throw FeatureNotAvailableException("Last homework is not available in API mode")
        }
    }

    suspend fun getLastExams(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastTests()
            Mode.API -> throw FeatureNotAvailableException("Last exams is not available in API mode")
        }
    }

    suspend fun getLastStudentLessons(): List<String> = withContext(Dispatchers.IO) {
        when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastStudentLessons()
            Mode.API -> throw FeatureNotAvailableException("Last student lesson is not available in API mode")
        }
    }
}
