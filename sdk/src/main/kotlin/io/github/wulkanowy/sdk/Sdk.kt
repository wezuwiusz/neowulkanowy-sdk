package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.mapper.*
import io.github.wulkanowy.sdk.mobile.Mobile
import io.github.wulkanowy.sdk.pojo.*
import io.github.wulkanowy.sdk.scrapper.Scrapper
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

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

    var androidVersion = "8.1.0"
        set(value) {
            field = value
            scrapper.androidVersion = value
        }

    var buildTag = "SM-J500H Build/LMY48B"
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
        addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            logger(it)
        }).setLevel(HttpLoggingInterceptor.Level.BASIC))
    }

    fun addInterceptor(interceptor: Interceptor, network: Boolean = false) {
        scrapper.addInterceptor(interceptor, network)
        mobile.setInterceptor(interceptor, network)
        interceptors.add(interceptor to network)
    }

    fun switchDiary(diaryId: Int, schoolYear: Int): Sdk {
        return also {
            it.diaryId = diaryId
            it.schoolYear = schoolYear
        }
    }

    suspend fun getPasswordResetCaptchaCode(registerBaseUrl: String, symbol: String) = scrapper.getPasswordResetCaptcha(registerBaseUrl, symbol)

    suspend fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): String {
        return scrapper.sendPasswordResetRequest(registerBaseUrl, symbol, email, captchaCode)
    }

    suspend fun getStudentsFromMobileApi(token: String, pin: String, symbol: String, firebaseToken: String, apiKey: String = ""): List<Student> {
        return mobile.getStudents(mobile.getCertificate(token, pin, symbol, buildTag, androidVersion, firebaseToken), apiKey).mapStudents(symbol)
    }

    suspend fun getStudentsFromScrapper(email: String, password: String, scrapperBaseUrl: String, symbol: String = "Default"): List<Student> {
        return scrapper.let {
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
    ): List<Student> {
        return getStudentsFromScrapper(email, password, scrapperBaseUrl, startSymbol)
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

    suspend fun getSemesters(): List<Semester> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().mapSemesters()
            Mode.API -> mobile.getStudents().mapSemesters(studentId)
        }
    }

    suspend fun getAttendance(startDate: LocalDate, endDate: LocalDate, semesterId: Int): List<Attendance> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).mapAttendance()
            Mode.HYBRID, Mode.API -> mobile.getAttendance(startDate, endDate, semesterId).mapAttendance(mobile.getDictionaries())
        }
    }

    suspend fun getAttendanceSummary(subjectId: Int? = -1): List<AttendanceSummary> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getAttendanceSummary(subjectId).mapAttendanceSummary()
            Mode.API -> throw FeatureNotAvailableException("Attendance summary is not available in API mode")
        }
    }

    suspend fun excuseForAbsence(absents: List<Absent>, content: String? = null): Boolean {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.excuseForAbsence(absents.mapToScrapperAbsent(), content)
            Mode.API -> throw FeatureNotAvailableException("Absence excusing is not available in API mode")
        }
    }

    suspend fun getSubjects(): List<Subject> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSubjects().mapSubjects()
            Mode.API -> mobile.getDictionaries().subjects.mapSubjects()
        }
    }

    suspend fun getExams(start: LocalDate, end: LocalDate, semesterId: Int): List<Exam> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getExams(start, end).mapExams()
            Mode.HYBRID, Mode.API -> mobile.getExams(start, end, semesterId).mapExams(mobile.getDictionaries())
        }
    }

    suspend fun getGrades(semesterId: Int): Pair<List<Grade>, List<GradeSummary>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).mapGrades()
            Mode.HYBRID, Mode.API -> mobile.getGrades(semesterId).mapGrades(mobile.getDictionaries())
        }
    }

    suspend fun getGradesDetails(semesterId: Int): List<Grade> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getGradesDetails(semesterId).mapGradesDetails()
            Mode.HYBRID, Mode.API -> mobile.getGradesDetails(semesterId).mapGradesDetails(mobile.getDictionaries())
        }
    }

    suspend fun getGradesSummary(semesterId: Int): List<GradeSummary> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getGradesSummary(semesterId).mapGradesSummary()
            Mode.HYBRID, Mode.API -> mobile.getGradesSummary(semesterId).mapGradesSummary(mobile.getDictionaries())
        }
    }

    suspend fun getGradesAnnualStatistics(semesterId: Int): List<GradeStatistics> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesAnnualStatistics(semesterId).mapGradeStatistics()
            Mode.API -> throw FeatureNotAvailableException("Class grades annual statistics is not available in API mode")
        }
    }

    suspend fun getGradesPartialStatistics(semesterId: Int): List<GradeStatistics> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPartialStatistics(semesterId).mapGradeStatistics()
            Mode.API -> throw FeatureNotAvailableException("Class grades partial statistics is not available in API mode")
        }
    }

    suspend fun getGradesPointsStatistics(semesterId: Int): List<GradePointsStatistics> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPointsStatistics(semesterId).mapGradePointsStatistics()
            Mode.API -> throw FeatureNotAvailableException("Class grades points statistics is not available in API mode")
        }
    }

    suspend fun getHomework(start: LocalDate, end: LocalDate, semesterId: Int = 0): List<Homework> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getHomework(start, end).mapHomework()
            Mode.HYBRID, Mode.API -> mobile.getHomework(start, end, semesterId).mapHomework(mobile.getDictionaries())
        }
    }

    suspend fun getNotes(semesterId: Int): List<Note> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getNotes().mapNotes()
            Mode.HYBRID, Mode.API -> mobile.getNotes(semesterId).mapNotes(mobile.getDictionaries())
        }
    }

    suspend fun getRegisteredDevices(): List<Device> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRegisteredDevices().mapDevices()
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    suspend fun getToken(): Token {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getToken().mapToken()
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    suspend fun unregisterDevice(id: Int): Boolean {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.unregisterDevice(id)
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    suspend fun getTeachers(semesterId: Int): List<Teacher> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getTeachers().mapTeachers()
            Mode.HYBRID, Mode.API -> mobile.getTeachers(studentId, semesterId).mapTeachers(mobile.getDictionaries())
        }
    }

    suspend fun getSchool(): School {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSchool().mapSchool()
            Mode.API -> throw FeatureNotAvailableException("School info is not available in API mode")
        }
    }

    suspend fun getStudentInfo(): StudentInfo {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentInfo().mapStudent()
            Mode.API -> throw FeatureNotAvailableException("Student info is not available in API mode")
        }
    }

    suspend fun getReportingUnits(): List<ReportingUnit> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getReportingUnits().mapReportingUnits()
            Mode.API -> mobile.getStudents().mapReportingUnits(studentId)
        }
    }

    suspend fun getRecipients(unitId: Int, role: Int = 2): List<Recipient> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRecipients(unitId, role).mapRecipients()
            Mode.API -> mobile.getDictionaries().teachers.mapRecipients(unitId)
        }
    }

    suspend fun getMessages(folder: Folder, start: LocalDateTime, end: LocalDateTime): List<Message> {
        return when (folder) {
            Folder.RECEIVED -> getReceivedMessages(start, end)
            Folder.SENT -> getSentMessages(start, end)
            Folder.TRASHED -> getDeletedMessages(start, end)
        }
    }

    suspend fun getReceivedMessages(start: LocalDateTime, end: LocalDateTime): List<Message> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getReceivedMessages().mapMessages() // TODO
            Mode.API -> mobile.getMessages(start, end).mapMessages(mobile.getDictionaries())
        }
    }

    suspend fun getSentMessages(start: LocalDateTime, end: LocalDateTime): List<Message> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSentMessages().mapMessages()
            Mode.API -> mobile.getMessagesSent(start, end).mapMessages(mobile.getDictionaries())
        }
    }

    suspend fun getDeletedMessages(start: LocalDateTime, end: LocalDateTime): List<Message> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getDeletedMessages().mapMessages()
            Mode.API -> mobile.getMessagesDeleted(start, end).mapMessages(mobile.getDictionaries())
        }
    }

    suspend fun getMessageRecipients(messageId: Int, senderId: Int): List<Recipient> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageRecipients(messageId, senderId).mapRecipients()
            Mode.API -> TODO()
        }
    }

    suspend fun getMessageDetails(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null): MessageDetails {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageDetails(messageId, folderId, read, id).mapScrapperMessage()
            Mode.API -> mobile.changeMessageStatus(messageId, when (folderId) {
                1 -> "Odebrane"
                2 -> "Wysłane"
                else -> "Usunięte"
            }, "Widoczna").let { MessageDetails("", emptyList()) }
        }
    }

    suspend fun sendMessage(subject: String, content: String, recipients: List<Recipient>): SentMessage {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.sendMessage(subject, content, recipients.mapFromRecipientsToScraper()).mapSentMessage()
            Mode.API -> mobile.sendMessage(subject, content, recipients.mapFromRecipientsToMobile()).mapSentMessage(loginId)
        }
    }

    suspend fun deleteMessages(messages: List<Pair<Int, Int>>): Boolean {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.deleteMessages(messages)
            Mode.HYBRID, Mode.API -> messages.map { (messageId, folderId) ->
                mobile.changeMessageStatus(messageId, when (folderId) {
                    1 -> "Odebrane"
                    2 -> "Wysłane"
                    else -> "Usunięte"
                }, "Usunieta")
            }.isNotEmpty()
        }
    }

    suspend fun getTimetable(start: LocalDate, end: LocalDate): List<Timetable> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetable(start, end).mapTimetable()
            Mode.HYBRID, Mode.API -> mobile.getTimetable(start, end, 0).mapTimetable(mobile.getDictionaries())
        }
    }

    suspend fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1): List<CompletedLesson> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getCompletedLessons(start, end, subjectId).mapCompletedLessons()
            Mode.API -> throw FeatureNotAvailableException("Completed lessons are not available in API mode")
        }
    }

    suspend fun getLuckyNumber(unitName: String = ""): Int {
        val numbers = getKidsLuckyNumbers()
        // if lucky number unitName match unit name from student tile
        numbers.singleOrNull { number -> number.unitName == unitName }?.let {
            return it.number
        }

        // if there there is only one lucky number and its doesn't match to any student
        if (numbers.size == 1) {
            return numbers.single().number
        }

        // if there is more than one lucky number, return first (just like this was working before 0.16.0)
        if (numbers.size > 1) {
            return numbers.first().number
        }

        // else
        return -1
    }

    suspend fun getSelfGovernments(): List<GovernmentUnit> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSelfGovernments().mapToUnits()
            Mode.API -> throw FeatureNotAvailableException("Self governments is not available in API mode")
        }
    }

    suspend fun getStudentThreats(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentThreats()
            Mode.API -> throw FeatureNotAvailableException("Student threats are not available in API mode")
        }
    }

    suspend fun getStudentsTrips(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentsTrips()
            Mode.API -> throw FeatureNotAvailableException("Students trips is not available in API mode")
        }
    }

    suspend fun getLastGrades(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastGrades()
            Mode.API -> throw FeatureNotAvailableException("Last grades is not available in API mode")
        }
    }

    suspend fun getFreeDays(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getFreeDays()
            Mode.API -> throw FeatureNotAvailableException("Free days is not available in API mode")
        }
    }

    suspend fun getKidsLuckyNumbers(): List<LuckyNumber> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLuckyNumbers().mapLuckyNumbers()
            Mode.API -> throw FeatureNotAvailableException("Kids Lucky number is not available in API mode")
        }
    }

    suspend fun getKidsTimetable(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLessonPlan()
            Mode.API -> throw FeatureNotAvailableException("Kids timetable is not available in API mode")
        }
    }

    suspend fun getLastHomework(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastHomework()
            Mode.API -> throw FeatureNotAvailableException("Last homework is not available in API mode")
        }
    }

    suspend fun getLastExams(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastTests()
            Mode.API -> throw FeatureNotAvailableException("Last exams is not available in API mode")
        }
    }

    suspend fun getLastStudentLessons(): List<String> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastStudentLessons()
            Mode.API -> throw FeatureNotAvailableException("Last student lesson is not available in API mode")
        }
    }
}
