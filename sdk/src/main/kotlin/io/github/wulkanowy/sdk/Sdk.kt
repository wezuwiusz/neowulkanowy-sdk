package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.exception.ScrapperExceptionTransformer
import io.github.wulkanowy.sdk.mapper.*
import io.github.wulkanowy.sdk.mobile.Mobile
import io.github.wulkanowy.sdk.pojo.*
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle
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

    fun getPasswordResetCaptchaCode(registerBaseUrl: String, symbol: String) = rxSingle { scrapper.getPasswordResetCaptcha(registerBaseUrl, symbol) }

    fun sendPasswordResetRequest(registerBaseUrl: String, symbol: String, email: String, captchaCode: String): Single<String> {
        return rxSingle { scrapper.sendPasswordResetRequest(registerBaseUrl, symbol, email, captchaCode) }
    }

    fun getStudentsFromMobileApi(token: String, pin: String, symbol: String, firebaseToken: String, apiKey: String = ""): Single<List<Student>> {
        return rxSingle { mobile.getStudents(mobile.getCertificate(token, pin, symbol, buildTag, androidVersion, firebaseToken), apiKey).mapStudents(symbol) }
    }

    fun getStudentsFromScrapper(email: String, password: String, scrapperBaseUrl: String, symbol: String = "Default"): Single<List<Student>> {
        return scrapper.let {
            it.baseUrl = scrapperBaseUrl
            it.email = email
            it.password = password
            it.symbol = symbol
            rxSingle { it.getStudents().mapStudents() }.compose(ScrapperExceptionTransformer())
        }
    }

    fun getStudentsHybrid(
        email: String,
        password: String,
        scrapperBaseUrl: String,
        firebaseToken: String,
        startSymbol: String = "Default",
        apiKey: String = ""
    ): Single<List<Student>> {
        return getStudentsFromScrapper(email, password, scrapperBaseUrl, startSymbol)
            .compose(ScrapperExceptionTransformer())
            .map { students -> students.distinctBy { it.symbol } }
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { scrapperStudent ->
                scrapper.let {
                    it.symbol = scrapperStudent.symbol
                    it.schoolSymbol = scrapperStudent.schoolSymbol
                    it.studentId = scrapperStudent.studentId
                    it.diaryId = -1
                    it.classId = scrapperStudent.classId
                    it.loginType = Scrapper.LoginType.valueOf(scrapperStudent.loginType.name)
                }
                rxSingle { scrapper.getToken() }.compose(ScrapperExceptionTransformer())
                    .flatMap { getStudentsFromMobileApi(it.token, it.pin, it.symbol, firebaseToken, apiKey) }
                    .map { apiStudents ->
                        apiStudents.map { student ->
                            student.copy(
                                loginMode = Mode.HYBRID,
                                loginType = scrapperStudent.loginType,
                                scrapperBaseUrl = scrapperStudent.scrapperBaseUrl
                            )
                        }
                    }
            }.toList().map { it.flatten() }
    }

    fun getSemesters(now: LocalDate = LocalDate.now()): Single<List<Semester>> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSemesters().mapSemesters()
                Mode.API -> mobile.getStudents().mapSemesters(studentId, now)
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getAttendance(startDate: LocalDate, endDate: LocalDate, semesterId: Int): Single<List<Attendance>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getAttendance(startDate, endDate).mapAttendance()
                Mode.HYBRID, Mode.API -> mobile.getAttendance(startDate, endDate, semesterId).mapAttendance(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getAttendanceSummary(subjectId: Int? = -1): Single<List<AttendanceSummary>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getAttendanceSummary(subjectId).mapAttendanceSummary() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Attendance summary is not available in API mode")
        }
    }

    fun excuseForAbsence(absents: List<Absent>, content: String? = null): Single<Boolean> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.excuseForAbsence(absents.mapToScrapperAbsent(), content) }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Absence excusing is not available in API mode")
        }
    }

    fun getSubjects(): Single<List<Subject>> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSubjects().mapSubjects()
                Mode.API -> mobile.getDictionaries().subjects.mapSubjects()
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getExams(start: LocalDate, end: LocalDate, semesterId: Int): Single<List<Exam>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getExams(start, end).mapExams()
                Mode.HYBRID, Mode.API -> mobile.getExams(start, end, semesterId).mapExams(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getGrades(semesterId: Int): Single<Pair<List<Grade>, List<GradeSummary>>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getGrades(semesterId).mapGrades()
                Mode.HYBRID, Mode.API -> mobile.getGrades(semesterId).mapGrades(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getGradesDetails(semesterId: Int): Single<List<Grade>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getGradesDetails(semesterId).mapGradesDetails()
                Mode.HYBRID, Mode.API -> mobile.getGradesDetails(semesterId).mapGradesDetails(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getGradesSummary(semesterId: Int): Single<List<GradeSummary>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getGradesSummary(semesterId).mapGradesSummary()
                Mode.HYBRID, Mode.API -> mobile.getGradesSummary(semesterId).mapGradesSummary(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getGradesAnnualStatistics(semesterId: Int): Single<List<GradeStatistics>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getGradesAnnualStatistics(semesterId).mapGradeStatistics() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Class grades annual statistics is not available in API mode")
        }
    }

    fun getGradesPartialStatistics(semesterId: Int): Single<List<GradeStatistics>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getGradesPartialStatistics(semesterId).mapGradeStatistics() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Class grades partial statistics is not available in API mode")
        }
    }

    fun getGradesPointsStatistics(semesterId: Int): Single<List<GradePointsStatistics>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getGradesPointsStatistics(semesterId).mapGradePointsStatistics() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Class grades points statistics is not available in API mode")
        }
    }

    fun getHomework(start: LocalDate, end: LocalDate, semesterId: Int = 0): Single<List<Homework>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getHomework(start, end).mapHomework()
                Mode.HYBRID, Mode.API -> mobile.getHomework(start, end, semesterId).mapHomework(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getNotes(semesterId: Int): Single<List<Note>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getNotes().mapNotes()
                Mode.HYBRID, Mode.API -> mobile.getNotes(semesterId).mapNotes(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getRegisteredDevices(): Single<List<Device>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getRegisteredDevices().mapDevices() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    fun getToken(): Single<Token> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getToken().mapToken() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    fun unregisterDevice(id: Int): Single<Boolean> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.unregisterDevice(id) }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Devices management is not available in API mode")
        }
    }

    fun getTeachers(semesterId: Int): Single<List<Teacher>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getTeachers().mapTeachers()
                Mode.HYBRID, Mode.API -> mobile.getTeachers(studentId, semesterId).mapTeachers(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getSchool(): Single<School> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getSchool().mapSchool() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("School info is not available in API mode")
        }
    }

    fun getStudentInfo(): Single<StudentInfo> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getStudentInfo().mapStudent()
                Mode.API -> throw FeatureNotAvailableException("Student info is not available in API mode")
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getReportingUnits(): Single<List<ReportingUnit>> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getReportingUnits().mapReportingUnits()
                Mode.API -> mobile.getStudents().mapReportingUnits(studentId)
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getRecipients(unitId: Int, role: Int = 2): Single<List<Recipient>> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getRecipients(unitId, role).mapRecipients()
                Mode.API -> mobile.getDictionaries().teachers.mapRecipients(unitId)
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getMessages(folder: Folder, start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return when (folder) {
            Folder.RECEIVED -> getReceivedMessages(start, end)
            Folder.SENT -> getSentMessages(start, end)
            Folder.TRASHED -> getDeletedMessages(start, end)
        }
    }

    fun getReceivedMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getReceivedMessages().mapMessages() // TODO
                Mode.API -> mobile.getMessages(start, end).mapMessages(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getSentMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getSentMessages().mapMessages()
                Mode.API -> mobile.getMessagesSent(start, end).mapMessages(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getDeletedMessages(start: LocalDateTime, end: LocalDateTime): Single<List<Message>> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getDeletedMessages().mapMessages()
                Mode.API -> mobile.getMessagesDeleted(start, end).mapMessages(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getMessageRecipients(messageId: Int, senderId: Int): Single<List<Recipient>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getMessageRecipients(messageId, senderId).mapRecipients() }.compose(ScrapperExceptionTransformer())
            Mode.API -> TODO()
        }
    }

    fun getMessageDetails(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null): Single<MessageDetails> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.getMessageDetails(messageId, folderId, read, id).mapScrapperMessage()
                Mode.API -> mobile.changeMessageStatus(messageId, when (folderId) {
                    1 -> "Odebrane"
                    2 -> "Wysłane"
                    else -> "Usunięte"
                }, "Widoczna").let { MessageDetails("", emptyList()) }
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> {
        return rxSingle {
            when (mode) {
                Mode.HYBRID, Mode.SCRAPPER -> scrapper.sendMessage(subject, content, recipients.mapFromRecipientsToScraper()).mapSentMessage()
                Mode.API -> mobile.sendMessage(subject, content, recipients.mapFromRecipientsToMobile()).mapSentMessage(loginId)
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun deleteMessages(messages: List<Pair<Int, Int>>): Single<Boolean> {
        return when (mode) {
            Mode.SCRAPPER -> rxSingle { scrapper.deleteMessages(messages) }.compose(ScrapperExceptionTransformer())
            Mode.HYBRID, Mode.API -> Completable.mergeDelayError(messages.map { (messageId, folderId) ->
                rxSingle {
                    mobile.changeMessageStatus(messageId, when (folderId) {
                        1 -> "Odebrane"
                        2 -> "Wysłane"
                        else -> "Usunięte"
                    }, "Usunieta")
                }.ignoreElement()
            }).toSingleDefault(true)
        }
    }

    fun getTimetable(start: LocalDate, end: LocalDate): Single<List<Timetable>> {
        return rxSingle {
            when (mode) {
                Mode.SCRAPPER -> scrapper.getTimetable(start, end).mapTimetable()
                Mode.HYBRID, Mode.API -> mobile.getTimetable(start, end, 0).mapTimetable(mobile.getDictionaries())
            }
        }.compose(ScrapperExceptionTransformer())
    }

    fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1): Single<List<CompletedLesson>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getCompletedLessons(start, end, subjectId).mapCompletedLessons() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Completed lessons are not available in API mode")
        }
    }

    fun getLuckyNumber(unitName: String = ""): Maybe<Int> {
        return getKidsLuckyNumbers().filter { it.isNotEmpty() }.flatMap { numbers ->
            // if lucky number unitName match unit name from student tile
            numbers.singleOrNull { number -> number.unitName == unitName }?.let {
                return@flatMap Maybe.just(it)
            }

            // if there there is only one lucky number and its doesn't match to any student
            if (numbers.size == 1) {
                return@flatMap Maybe.just(numbers.single())
            }

            // if there is more than one lucky number, return first (just like this was working before 0.16.0)
            if (numbers.size > 1) {
                return@flatMap Maybe.just(numbers.first())
            }

            // else
            Maybe.empty<LuckyNumber>()
        }.map { it.number }
    }

    fun getSelfGovernments(): Single<List<GovernmentUnit>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getSelfGovernments().mapToUnits() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Self governments is not available in API mode")
        }
    }

    fun getStudentThreats(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getStudentThreats() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Student threats are not available in API mode")
        }
    }

    fun getStudentsTrips(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getStudentsTrips() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Students trips is not available in API mode")
        }
    }

    fun getLastGrades(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getLastGrades() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last grades is not available in API mode")
        }
    }

    fun getFreeDays(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getFreeDays() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Free days is not available in API mode")
        }
    }

    fun getKidsLuckyNumbers(): Single<List<LuckyNumber>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getKidsLuckyNumbers().mapLuckyNumbers() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Kids Lucky number is not available in API mode")
        }
    }

    fun getKidsTimetable(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getKidsLessonPlan() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Kids timetable is not available in API mode")
        }
    }

    fun getLastHomework(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getLastHomework() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last homework is not available in API mode")
        }
    }

    fun getLastExams(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getLastTests() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last exams is not available in API mode")
        }
    }

    fun getLastStudentLessons(): Single<List<String>> {
        return when (mode) {
            Mode.HYBRID, Mode.SCRAPPER -> rxSingle { scrapper.getLastStudentLessons() }.compose(ScrapperExceptionTransformer())
            Mode.API -> throw FeatureNotAvailableException("Last student lesson is not available in API mode")
        }
    }
}
