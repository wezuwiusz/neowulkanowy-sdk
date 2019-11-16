package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.exception.ScrapperExceptionTransformer
import io.github.wulkanowy.sdk.mapper.*
import io.github.wulkanowy.sdk.mobile.Mobile
import io.github.wulkanowy.sdk.pojo.*
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.attendance.Absent
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

    private val mobile = Mobile()

    private val scrapper = Scrapper().apply {
        useNewStudent = true
    }

    var mode = Mode.HYBRID

    var apiBaseUrl = ""
        set(value) {
            field = value
            mobile.apiBaseUrl = value
        }

    var deviceName = "Wulkanowy SDK"
        set(value) {
            field = value
            mobile.deviceName = value
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
            mobile.diaryId = value
        }

    var symbol = ""
        set(value) {
            field = value
            scrapper.symbol = value
            mobile.symbol = value
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

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf()

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false, index: Int = -1) {
        scrapper.setInterceptor(interceptor, network, index)
        mobile.setInterceptor(interceptor, network)
        interceptors.add(interceptor to network)
    }

    fun getStudentsFromMobileApi(token: String, pin: String, symbol: String, apiKey: String): Single<List<Student>> {
        return mobile.getCertificate(token, pin, symbol)
            .flatMap { mobile.getStudents(it, apiKey) }
            .map { it.mapStudents(symbol) }
    }

    fun getStudentsFromScrapper(email: String, password: String, ssl: Boolean, host: String, symbol: String = "Default"): Single<List<Student>> {
        return scrapper.run {
            this.ssl = ssl
            this.host = host
            this.email = email
            this.password = password
            this.symbol = symbol
            getStudents().compose(ScrapperExceptionTransformer()).map { it.mapStudents(ssl, scrapperHost) }
        }
    }

    fun getStudentsHybrid(email: String, password: String, apiKey: String, ssl: Boolean, host: String, startSymbol: String = "Default"): Single<List<Student>> {
        return getStudentsFromScrapper(email, password, ssl, host, startSymbol)
            .compose(ScrapperExceptionTransformer())
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { scrapperStudent ->
                scrapper.run {
                    symbol = scrapperStudent.symbol
                    schoolSymbol = scrapperStudent.schoolSymbol
                    studentId = scrapperStudent.studentId
                    diaryId = -1
                    classId = scrapperStudent.classId
                    loginType = Scrapper.LoginType.valueOf(scrapperStudent.loginType.name)
                }
                scrapper.getToken().compose(ScrapperExceptionTransformer())
                    .flatMap { getStudentsFromMobileApi(it.token, it.pin, it.symbol, apiKey) }
                    .map { apiStudents ->
                        apiStudents.map { student ->
                            student.copy(
                                loginMode = Mode.HYBRID,
                                scrapperHost = scrapperStudent.scrapperHost,

                                classId = scrapperStudent.classId,
                                className = scrapperStudent.className
                            )
                        }
                    }
            }.toList().map { it.flatten() }
    }

    fun getSemesters(): Single<List<Semester>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().compose(ScrapperExceptionTransformer()).map { it.mapSemesters() }
            Mode.API -> mobile.getStudents().map { it.mapSemesters(studentId) }
        }
    }

    fun getAttendance(startDate: LocalDate, endDate: LocalDate, semesterId: Int): Single<List<Attendance>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).compose(ScrapperExceptionTransformer()).map { it.mapAttendance() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getAttendance(startDate, endDate, classId, semesterId, studentId).map { it.mapAttendance(dict) }
            }
        }
    }

    fun getAttendanceSummary(subjectId: Int? = -1): Single<List<AttendanceSummary>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getAttendanceSummary(subjectId).compose(ScrapperExceptionTransformer()).map { it.mapAttendanceSummary() }
            Mode.API -> throw FeatureNotAvailableException("Attendance summary is not available in API mode")
        }
    }

    fun excuseForAbsence(absents: List<Absent>, content: String? = null): Single<Boolean> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.excuseForAbsence(absents, content).compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Absence excusing is not available in API mode")
        }
    }

    fun getSubjects(): Single<List<Subject>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getSubjects().compose(ScrapperExceptionTransformer()).map { it.mapSubjects() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().map { it.subjects }.map { it.mapSubjects() }
        }
    }

    fun getExams(start: LocalDate, end: LocalDate, semesterId: Int): Single<List<Exam>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getExams(start, end).compose(ScrapperExceptionTransformer()).map { it.mapExams() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getExams(start, end, classId, semesterId, studentId).map { it.mapExams(dict) }
            }
        }
    }

    fun getGrades(semesterId: Int): Single<List<Grade>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).compose(ScrapperExceptionTransformer()).map { grades -> grades.mapGrades() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getGrades(classId, semesterId, studentId).map { it.mapGrades(dict) }
            }
        }
    }

    fun getGradesSummary(semesterId: Int): Single<List<GradeSummary>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getGradesSummary(semesterId).compose(ScrapperExceptionTransformer()).map { it.mapGradesSummary() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getGradesSummary(classId, semesterId, studentId).map { it.mapGradesSummary(dict) }
            }
        }
    }

    fun getGradesAnnualStatistics(semesterId: Int): Single<List<GradeStatistics>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesAnnualStatistics(semesterId).compose(ScrapperExceptionTransformer()).map { it.mapGradeStatistics() }
            Mode.API -> throw FeatureNotAvailableException("Class grades annual statistics is not available in API mode")
        }
    }

    fun getGradesPartialStatistics(semesterId: Int): Single<List<GradeStatistics>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPartialStatistics(semesterId).compose(ScrapperExceptionTransformer()).map { it.mapGradeStatistics() }
            Mode.API -> throw FeatureNotAvailableException("Class grades partial statistics is not available in API mode")
        }
    }

    fun getGradesPointsStatistics(semesterId: Int): Single<List<GradePointsStatistics>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getGradesPointsStatistics(semesterId).compose(ScrapperExceptionTransformer()).map { it.mapGradePointsStatistics() }
            Mode.API -> throw FeatureNotAvailableException("Class grades points statistics is not available in API mode")
        }
    }

    fun getHomework(start: LocalDate, end: LocalDate, semesterId: Int = 0): Single<List<Homework>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getHomework(start, end).compose(ScrapperExceptionTransformer()).map { it.mapHomework() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getHomework(start, end, classId, semesterId, studentId).map { it.mapHomework(dict) }
            }
        }
    }

    fun getNotes(semesterId: Int): Single<List<Note>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getNotes().compose(ScrapperExceptionTransformer()).map { it.mapNotes() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getNotes(semesterId, studentId).map { it.mapNotes(dict) }
            }
        }
    }

    fun getRegisteredDevices(): Single<List<Device>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRegisteredDevices().compose(ScrapperExceptionTransformer()).map { it.mapDevices() }
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    fun getToken(): Single<Token> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getToken().compose(ScrapperExceptionTransformer()).map { it.mapToken() }
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    fun unregisterDevice(id: Int): Single<Boolean> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.unregisterDevice(id).compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    fun getTeachers(semesterId: Int): Single<List<Teacher>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getTeachers().compose(ScrapperExceptionTransformer()).map { it.mapTeachers() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getTeachers(studentId, semesterId).map { it.mapTeachers(dict) }
            }
        }
    }

    fun getSchool(): Single<School> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSchool().compose(ScrapperExceptionTransformer()).map { it.mapSchool() }
            Mode.API -> throw FeatureNotAvailableException("School info is not available in API mode")
        }
    }

    fun getStudentInfo(): Single<StudentInfo> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentInfo().compose(ScrapperExceptionTransformer()).map { it.mapStudent() }
            Mode.API -> throw FeatureNotAvailableException("Student info is not available in API mode")
        }
    }

    fun getReportingUnits(): Single<List<ReportingUnit>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getReportingUnits().compose(ScrapperExceptionTransformer()).map { it.mapReportingUnits() }
            Mode.API -> mobile.getStudents().map { it.mapReportingUnits(studentId) }
        }
    }

    fun getRecipients(unitId: Int, role: Int = 2): Single<List<Recipient>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRecipients(unitId, role).compose(ScrapperExceptionTransformer()).map { it.mapRecipients() }
            Mode.API -> mobile.getDictionaries().map { it.teachers }.map { it.mapRecipients(unitId) }
        }
    }

    fun getMessages(folder: Folder, start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (folder) {
            Folder.RECEIVED -> getReceivedMessages(start, end)
            Folder.SENT -> getSentMessages(start, end)
            Folder.TRASHED -> getDeletedMessages(start, end)
        }
    }

    fun getReceivedMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getReceivedMessages(start, end).compose(ScrapperExceptionTransformer()).map { it.mapMessages() }
            Mode.HYBRID, Mode.API -> mobile.getMessages(start, end, loginId, studentId).map { it.mapMessages() }
        }
    }

    fun getSentMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getSentMessages(start, end).compose(ScrapperExceptionTransformer()).map { it.mapMessages() }
            Mode.HYBRID, Mode.API -> mobile.getMessagesSent(start, end, loginId, studentId).map { it.mapMessages() }
        }
    }

    fun getDeletedMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getDeletedMessages(start, end).compose(ScrapperExceptionTransformer()).map { it.mapMessages() }
            Mode.HYBRID, Mode.API -> mobile.getMessagesDeleted(start, end, loginId, studentId).map { it.mapMessages() }
        }
    }

    fun getMessageRecipients(messageId: Int, senderId: Int): Single<List<Recipient>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageRecipients(messageId, senderId).compose(ScrapperExceptionTransformer()).map { it.mapRecipients() }
            Mode.API -> TODO()
        }
    }

    fun getMessageContent(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null): Single<String> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.getMessageContent(messageId, folderId, read, id).compose(ScrapperExceptionTransformer())
            Mode.HYBRID, Mode.API -> mobile.changeMessageStatus(messageId, when (folderId) {
                1 -> "Odebrane"
                2 -> "Wysłane"
                else -> "Usunięte"
            }, "Widoczna", loginId, studentId)
        }
    }

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.sendMessage(subject,
                content,
                recipients.mapFromRecipients()).compose(ScrapperExceptionTransformer()).map { it.mapSentMessage() }
            Mode.API -> TODO()
        }
    }

    fun deleteMessages(messages: List<Pair<Int, Int>>): Single<Boolean> {
        return when (mode) {
            Mode.SCRAPPER -> scrapper.deleteMessages(messages).compose(ScrapperExceptionTransformer())
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
            Mode.SCRAPPER -> scrapper.getTimetable(start, end).compose(ScrapperExceptionTransformer()).map { it.mapTimetable() }
            Mode.HYBRID, Mode.API -> mobile.getDictionaries().flatMap { dict ->
                mobile.getTimetable(start, end, classId, 0, studentId).map { it.mapTimetable(dict) }
            }
        }
    }

    fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1): Single<List<CompletedLesson>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getCompletedLessons(start, end, subjectId).compose(ScrapperExceptionTransformer()).map { it.mapCompletedLessons() }
            Mode.API -> throw FeatureNotAvailableException("Completed lessons are not available in API mode")
        }
    }

    @Deprecated("Deprecated due to VULCAN homepage update 19.06", ReplaceWith("getKidsLuckyNumbers()"))
    fun getLuckyNumber(): Maybe<Int> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLuckyNumber()
            Mode.API -> throw FeatureNotAvailableException("Lucky number is not available in API mode")
        }
    }

    fun getSelfGovernments(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSelfGovernments().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Self governments is not available in API mode")
        }
    }

    fun getStudentsTrips(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentsTrips().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Students trips is not available in API mode")
        }
    }

    fun getLastGrades(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastGrades().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last grades is not available in API mode")
        }
    }

    fun getFreeDays(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getFreeDays().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Free days is not available in API mode")
        }
    }

    fun getKidsLuckyNumbers(): Single<List<LuckyNumber>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLuckyNumbers().compose(ScrapperExceptionTransformer()).map { it.mapLuckyNumbers() }
            Mode.API -> throw FeatureNotAvailableException("Kids Lucky number is not available in API mode")
        }
    }

    fun getKidsTimetable(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getKidsLessonPlan().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Kids timetable is not available in API mode")
        }
    }

    fun getLastHomework(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastHomework().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last homework is not available in API mode")
        }
    }

    fun getLastExams(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastTests().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last exams is not available in API mode")
        }
    }

    fun getLastStudentLessons(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> scrapper.getLastStudentLessons().compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last student lesson is not available in API mode")
        }
    }
}
