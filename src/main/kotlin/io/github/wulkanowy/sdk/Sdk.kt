package io.github.wulkanowy.sdk

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.attendance.Absent
import io.github.wulkanowy.api.attendance.AttendanceSummary
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.api.messages.Folder
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.api.messages.SentMessage
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.mobile.TokenResponse
import io.github.wulkanowy.api.resettableLazy
import io.github.wulkanowy.api.resettableManager
import io.github.wulkanowy.api.school.School
import io.github.wulkanowy.api.school.Teacher
import io.github.wulkanowy.api.student.StudentInfo
import io.github.wulkanowy.api.timetable.CompletedLesson
import io.github.wulkanowy.sdk.attendance.mapAttendance
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.dictionaries.mapSubjects
import io.github.wulkanowy.sdk.exams.mapExams
import io.github.wulkanowy.sdk.grades.mapGrades
import io.github.wulkanowy.sdk.grades.mapGradesSummary
import io.github.wulkanowy.sdk.homework.mapHomework
import io.github.wulkanowy.sdk.messages.mapMessages
import io.github.wulkanowy.sdk.notes.mapNotes
import io.github.wulkanowy.sdk.pojo.*
import io.github.wulkanowy.sdk.register.mapSemesters
import io.github.wulkanowy.sdk.register.mapStudents
import io.github.wulkanowy.sdk.repository.RegisterRepository
import io.github.wulkanowy.sdk.repository.RepositoryManager
import io.github.wulkanowy.sdk.timetable.mapTimetable
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
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
        ADFSLightScoped
    }

    var mode = Mode.HYBRID

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

    var ssl = true
        set(value) {
            field = value
            scrapper.ssl = value
        }

    var scrapperHost = "fakelog.cf"
        set(value) {
            field = value
            scrapper.host = value
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

    var loginId = 0

    var diaryId = 0
        set(value) {
            field = value
            scrapper.diaryId = value
        }
    var symbol = ""
        set(value) {
            field = value
            scrapper.symbol = value
        }

    var loginType = ScrapperLoginType.AUTO
        set(value) {
            field = value
            scrapper.loginType = Api.LoginType.valueOf(value.name)
        }

    var logLevel = HttpLoggingInterceptor.Level.BASIC
        set(value) {
            field = value
            scrapper.logLevel = value
        }

    var androidVersion = ""
        set(value) {
            field = value
            scrapper.androidVersion = value
        }

    var buildTag = ""
        set(value) {
            field = value
            scrapper.buildTag = value
        }

    private val scrapper = Api().apply {
        useNewStudent = true
    }

    private val resettableManager = resettableManager()

    private val serviceManager by resettableLazy(resettableManager) {
        RepositoryManager(logLevel, apiKey, certificate, certKey, interceptors, apiBaseUrl, schoolSymbol)
    }

    private val routes by resettableLazy(resettableManager) {
        serviceManager.getRoutesRepository()
    }

    private val mobile by resettableLazy(resettableManager) {
        serviceManager.getMobileRepository()
    }

    private fun getRegisterRepo(host: String, symbol: String): RegisterRepository {
        return serviceManager.getRegisterRepository(host, symbol)
    }

    private lateinit var dictionaries: Dictionaries

    private fun getDictionaries(): Single<Dictionaries> {
        if (::dictionaries.isInitialized) return Single.just(dictionaries)

        return mobile.getDictionaries(0, 0, 0).map {
            it.apply { dictionaries = this }
        }
    }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false, index: Int = -1) {
        scrapper.setInterceptor(interceptor, network, index)
        interceptors.add(interceptor to network)
    }

    fun getStudents(): Single<List<Student>> {
        return when (mode) {
            Mode.API -> getApiStudents(token, pin, symbol)
            Mode.SCRAPPER -> {
                scrapper.run {
                    ssl = this@Sdk.ssl
                    host = this@Sdk.scrapperHost
                    email = this@Sdk.email
                    password = this@Sdk.password
                    getStudents().map { it.mapStudents(ssl, scrapperHost) }
                }
            }
            Mode.HYBRID -> {
                scrapper.run {
                    ssl = this@Sdk.ssl
                    host = this@Sdk.scrapperHost
                    email = this@Sdk.email
                    password = this@Sdk.password
                    getStudents().flatMapObservable { Observable.fromIterable(it) }.flatMapSingle {
                        scrapper.run {
                            symbol = it.symbol
                            schoolSymbol = it.schoolSymbol
                            studentId = it.studentId
                            diaryId = -1
                            classId = it.classId
                            loginType = it.loginType
                        }
                        scrapper.getToken().flatMap {
                            getApiStudents(it.token, it.pin, it.symbol)
                        }.map { apiStudents ->
                            apiStudents.map { apiStudent ->
                                apiStudent.copy(loginMode = Mode.HYBRID, scrapperHost = scrapperHost)
                            }
                        }
                    }.toList().map { it.flatten() }
                }
            }
        }
    }

    private fun getApiStudents(token: String, pin: String, symbol: String): Single<List<Student>> {
        return routes.getRouteByToken(token).flatMap {
            this@Sdk.apiBaseUrl = it
            this@Sdk.symbol = symbol
            getRegisterRepo(apiBaseUrl, symbol).getCertificate(token, pin, deviceName)
        }.flatMap { certificateResponse ->
            if (certificateResponse.isError) throw RuntimeException(certificateResponse.message)
            this@Sdk.certKey = certificateResponse.tokenCert!!.certificateKey
            this@Sdk.certificate = certificateResponse.tokenCert.certificatePfx
            getRegisterRepo(apiBaseUrl, this@Sdk.symbol).getPupils().map { it.mapStudents(symbol, certificateResponse) }
        }
    }

    fun getSemesters(): Single<List<Semester>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().map { it.mapSemesters() }
            Mode.API -> getRegisterRepo(apiBaseUrl.replace("/$symbol", ""), symbol).getPupils().map { it.mapSemesters(studentId) }
        }
    }

    fun getAttendance(startDate: LocalDate, endDate: LocalDate, semesterId: Int): Single<List<Attendance>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).map { it.mapAttendance() }
            Mode.HYBRID, Mode.API -> getDictionaries().flatMap { dict ->
                mobile.getAttendance(startDate, endDate, classId, semesterId, studentId).map { it.mapAttendance(dict) }
            }
        }
    }

    fun getAttendanceSummary(subjectId: Int? = -1): Single<List<AttendanceSummary>> = scrapper.getAttendanceSummary(subjectId)

    fun excuseForAbsence(absents: List<Absent>, content: String? = null): Single<Boolean> = scrapper.excuseForAbsence(absents, content)

    fun getSubjects(): Single<List<Subject>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getSubjects().map { it.mapSubjects() }
            Mode.HYBRID, Mode.API -> getDictionaries().map { it.subjects }.map { it.mapSubjects() }
        }
    }

    fun getExams(start: LocalDate, end: LocalDate, semesterId: Int): Single<List<Exam>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getExams(start, end).map { it.mapExams() }
            Mode.HYBRID, Mode.API -> getDictionaries().flatMap { dict ->
                mobile.getExams(start, end, classId, semesterId, studentId).map { it.mapExams(dict) }
            }
        }
    }

    fun getGrades(semesterId: Int): Single<List<Grade>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).map { grades -> grades.mapGrades() }
            Mode.HYBRID, Mode.API -> getDictionaries().flatMap { dict ->
                mobile.getGrades(classId, semesterId, studentId).map { it.mapGrades(dict) }
            }
        }
    }

    fun getGradesSummary(semesterId: Int): Single<List<GradeSummary>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getGradesSummary(semesterId).map { it.mapGradesSummary() }
            Mode.HYBRID, Mode.API -> getDictionaries().flatMap { dict ->
                mobile.getGradesSummary(classId, semesterId, studentId).map { it.mapGradesSummary(dict) }
            }
        }
    }

    fun getGradesStatistics(semesterId: Int, annual: Boolean = false): Single<List<GradeStatistics>> = scrapper.getGradesStatistics(semesterId, annual)

    fun getHomework(start: LocalDate, end: LocalDate, semesterId: Int = 0): Single<List<Homework>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getHomework(start, end).map { it.mapHomework() }
            Mode.HYBRID, Mode.API -> getDictionaries().flatMap { dict ->
                mobile.getHomework(start, end, classId, semesterId, studentId).map { it.mapHomework(dict) }
            }
        }
    }

    fun getNotes(semesterId: Int): Single<List<Note>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getNotes().map { it.mapNotes() }
            Mode.HYBRID, Mode.API -> getDictionaries().flatMap { dict ->
                mobile.getNotes(semesterId, studentId).map { it.mapNotes(dict) }
            }
        }
    }

    fun getRegisteredDevices(): Single<List<Device>> = scrapper.getRegisteredDevices()

    fun getToken(): Single<TokenResponse> = scrapper.getToken()

    fun unregisterDevice(id: Int): Single<Boolean> = scrapper.unregisterDevice(id)

    fun getTeachers(): Single<List<Teacher>> = scrapper.getTeachers()

    fun getSchool(): Single<School> = scrapper.getSchool()

    fun getStudentInfo(): Single<StudentInfo> = scrapper.getStudentInfo()

    fun getReportingUnits(): Single<List<ReportingUnit>> = scrapper.getReportingUnits()

    fun getRecipients(unitId: Int, role: Int = 2): Single<List<Recipient>> = scrapper.getRecipients(unitId, role)

    fun getMessages(folder: Folder, start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (folder) {
            Folder.RECEIVED -> getReceivedMessages(start, end)
            Folder.SENT -> getSentMessages(start, end)
            Folder.TRASHED -> getDeletedMessages(start, end)
        }
    }

    fun getReceivedMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getReceivedMessages(start, end).map { it.mapMessages() }
            Mode.HYBRID, Mode.API -> mobile.getMessages(start, end, loginId, studentId).map { it.mapMessages() }
        }
    }

    fun getSentMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getSentMessages(start, end).map { it.mapMessages() }
            Mode.HYBRID, Mode.API -> mobile.getMessagesSent(start, end, loginId, studentId).map { it.mapMessages() }
        }
    }

    fun getDeletedMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getDeletedMessages(start, end).map { it.mapMessages() }
            Mode.HYBRID, Mode.API -> mobile.getMessagesDeleted(start, end, loginId, studentId).map { it.mapMessages() }
        }
    }

    fun getMessageRecipients(messageId: Int, senderId: Int): Single<List<Recipient>> = scrapper.getMessageRecipients(messageId, senderId)

    fun getMessageContent(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null): Single<String> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getMessageContent(messageId, folderId, read, id)
            Mode.HYBRID, Mode.API -> mobile.changeMessageStatus(messageId, when (folderId) {
                1 -> "Odebrane"
                2 -> "Wysłane"
                else -> "Usunięte"
            }, "Widoczna", loginId, studentId)
        }
    }

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> = scrapper.sendMessage(subject, content, recipients)

    fun deleteMessages(messages: List<Pair<Int, Int>>): Single<Boolean> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.deleteMessages(messages)
            Mode.HYBRID, Mode.API -> Completable.mergeDelayError(messages.map { (messageId, folderId) ->
                mobile.changeMessageStatus(messageId, when (folderId) {
                    1 -> "Odebrane"
                    2 -> "Wysłane"
                    else -> "Usunięte"
                }, "Usunieta", loginId, studentId).toMaybe().ignoreElement()
            }).toSingleDefault(true).onErrorReturnItem(false)
        }
    }

    fun getTimetable(start: LocalDate, end: LocalDate): Single<List<Timetable>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getTimetable(start, end).map { it.mapTimetable() }
            Mode.HYBRID, Mode.API -> getDictionaries().flatMap { dict ->
                mobile.getTimetable(start, end, classId, 0, studentId).map { it.mapTimetable(dict) }
            }
        }
    }

    fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1): Single<List<CompletedLesson>> = scrapper.getCompletedLessons(start, end, subjectId)

    fun getLuckyNumber(): Maybe<Int> = scrapper.getLuckyNumber()
}
