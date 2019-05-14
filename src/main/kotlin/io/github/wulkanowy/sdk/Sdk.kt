package io.github.wulkanowy.sdk

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.attendance.Absent
import io.github.wulkanowy.api.messages.Folder
import io.github.wulkanowy.api.messages.Recipient
import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.api.resettableLazy
import io.github.wulkanowy.api.resettableManager
import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.pojo.Exam
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.sdk.repository.MobileRepository
import io.github.wulkanowy.sdk.repository.RegisterRepository
import io.github.wulkanowy.sdk.repository.RoutingRulesRepository
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

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

    private val routes by lazy {
        RoutingRulesRepository(getRetrofitBuilder().baseUrl("http://komponenty.vulcan.net.pl").build().create())
    }

    private val register by lazy(resettableManager) {
        RegisterRepository(getRetrofitBuilder().baseUrl("$apiBaseUrl/$symbol/mobile-api/Uczen.v3.UczenStart/").build().create())
    }

    private val mobile by resettableLazy(resettableManager) {
        MobileRepository(getRetrofitBuilder().baseUrl("$apiBaseUrl/$schoolSymbol/mobile-api/Uczen.v3.Uczen/").build().create())
    }

    private fun getRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                        .addInterceptor(SignInterceptor(apiKey, certificate, certKey))
                        .apply {
                            interceptors.forEach {
                                if (it.second) addNetworkInterceptor(it.first)
                                else addInterceptor(it.first)
                            }
                        }
                        .build()
                )
    }

    private fun Long.toLocalDate() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

    private fun getDictionaries() = mobile.getDictionaries(0, 0, 0)

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
                    getStudents().map { students ->
                        students.map {
                            Student(
                                    email = it.email,
                                    className = it.className,
                                    classId = it.classId,
                                    studentId = it.studentId,
                                    symbol = it.symbol,
                                    loginType = it.loginType,
                                    schoolName = it.schoolName,
                                    schoolSymbol = it.schoolSymbol,
                                    studentName = it.studentName,
                                    loginMode = Mode.SCRAPPER,
                                    ssl = ssl,
                                    apiHost = "",
                                    scrapperHost = scrapperHost,
                                    certificateKey = "",
                                    certificate = ""
                            )
                        }
                    }
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
            register.getCertificate(token, pin, deviceName)
        }.flatMap { certificateResponse ->
            if (certificateResponse.isError) throw RuntimeException(certificateResponse.message)
            this@Sdk.certKey = certificateResponse.tokenCert!!.certificateKey
            this@Sdk.certificate = certificateResponse.tokenCert.certificatePfx
            register.getPupils().map { students ->
                students.map {
                    Student(
                            email = it.userLogin,
                            symbol = symbol,
                            studentId = it.id,
                            classId = it.classId,
                            className = it.classCode,
                            studentName = "${it.name} ${it.surname}",
                            schoolSymbol = it.reportingUnitSymbol,
                            schoolName = it.reportingUnitName,
                            loginType = Api.LoginType.STANDARD,
                            loginMode = Mode.API,
                            apiHost = certificateResponse.tokenCert.apiEndpoint.removeSuffix("/"),
                            scrapperHost = "",
                            ssl = certificateResponse.tokenCert.apiEndpoint.startsWith("https"),
                            certificate = certificateResponse.tokenCert.certificatePfx,
                            certificateKey = certificateResponse.tokenCert.certificateKey
                    )
                }
            }
        }
    }

    fun getSemesters() = scrapper.getSemesters()

    fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null) = scrapper.getAttendance(startDate, endDate)

    fun getAttendanceSummary(subjectId: Int? = -1) = scrapper.getAttendanceSummary(subjectId)

    fun excuseForAbsence(absents: List<Absent>, content: String? = null) = scrapper.excuseForAbsence(absents, content)

    fun getSubjects() = scrapper.getSubjects()

    fun getExams(startDate: LocalDate, endDate: LocalDate): Single<List<Exam>> {
        return when (mode) {
            Mode.API -> getApiExams(startDate, endDate)
            Mode.SCRAPPER -> scrapper.getExams(startDate, endDate).map { exams ->
                exams.map {
                    Exam(
                            date = it.date.toLocalDate(),
                            entryDate = it.entryDate.toLocalDate(),
                            description = it.description,
                            group = it.group,
                            teacherSymbol = it.teacherSymbol,
                            teacher = it.teacher,
                            subject = it.subject,
                            type = it.type
                    )
                }
            }
            Mode.HYBRID -> getApiExams(startDate, endDate)
        }
    }

    private fun getApiExams(start: LocalDate, end: LocalDate): Single<List<Exam>> {
        return getDictionaries().flatMap { dict ->
            mobile.getExams(start, end, classId, 1, studentId).map { exams ->
                exams.map { exam ->
                    Exam(
                            date = exam.date.toLocalDate(),
                            entryDate = exam.date.toLocalDate(),
                            description = exam.description,
                            group = exam.divideName.orEmpty(),
                            teacher = dict.teachers.singleOrNull { it.loginId == exam.employeeId }?.run { "$name $surname" }.orEmpty(),
                            subject = dict.subjects.singleOrNull { it.id == exam.subjectId }?.name.orEmpty(),
                            teacherSymbol = dict.teachers.singleOrNull { it.loginId == exam.employeeId }?.code.orEmpty(),
                            type = if (exam.type) "Sprawdzian" else "Kartkówka"
                    )
                }
            }
        }
    }

    fun getGrades(semesterId: Int): Single<List<Grade>> {
        return when (mode) {
            Mode.API -> getApiGrades(semesterId)
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).map { grades ->
                grades.map {
                    Grade(
                            subject = it.subject,
                            description = it.description.orEmpty(),
                            symbol = it.symbol.orEmpty(),
                            comment = it.comment,
                            date = it.date.toLocalDate(),
                            teacher = it.teacher,
                            entry = it.entry,
                            weight = it.weight,
                            weightValue = it.weightValue,
                            color = it.color,
                            value = it.value.toDouble(),
                            modifier = it.modifier
                    )
                }
            }
            Mode.HYBRID -> getApiGrades(semesterId)
        }
    }

    private fun getApiGrades(semesterId: Int): Single<List<Grade>> {
        return getDictionaries().flatMap { dict ->
            mobile.getGrades(classId, semesterId, studentId).map { grades ->
                grades.map { grade ->
                    Grade(
                            subject = dict.subjects.singleOrNull { it.id == grade.subjectId }?.name.orEmpty(),
                            description = dict.gradeCategories.singleOrNull { it.id == grade.categoryId }?.name.orEmpty(),
                            symbol = dict.gradeCategories.singleOrNull { it.id == grade.categoryId }?.code.orEmpty(),
                            comment = grade.comment.orEmpty(),
                            date = grade.creationDate.toLocalDate(),
                            teacher = dict.teachers.singleOrNull { it.id == grade.employeeIdD }?.let { "${it.name} ${it.surname}" }.orEmpty(),
                            entry = grade.entry,
                            weightValue = grade.gradeWeight,
                            modifier = grade.modificationWeight ?: .0,
                            value = grade.value,
                            weight = grade.weight,
                            color = "0"
                    )
                }
            }
        }
    }

    fun getGradesSummary(semesterId: Int? = null) = scrapper.getGradesSummary(semesterId)

    fun getGradesStatistics(semesterId: Int, annual: Boolean = false) = scrapper.getGradesStatistics(semesterId, annual)

    fun getHomework(start: LocalDate, end: LocalDate? = null) = scrapper.getHomework(start, end)

    fun getNotes() = scrapper.getNotes()

    fun getRegisteredDevices() = scrapper.getRegisteredDevices()

    fun getToken() = scrapper.getToken()

    fun unregisterDevice(id: Int) = scrapper.unregisterDevice(id)

    fun getTeachers() = scrapper.getTeachers()

    fun getSchool() = scrapper.getSchool()

    fun getStudentInfo() = scrapper.getStudentInfo()

    fun getReportingUnits() = scrapper.getReportingUnits()

    fun getRecipients(unitId: Int, role: Int = 2) = scrapper.getRecipients(unitId, role)

    fun getMessages(folder: Folder, start: LocalDateTime? = null, end: LocalDateTime? = null) = scrapper.getMessages(folder, start, end)

    fun getReceivedMessages(start: LocalDateTime? = null, end: LocalDateTime? = null) = scrapper.getReceivedMessages(start, end)

    fun getSentMessages(start: LocalDateTime? = null, end: LocalDateTime? = null) = scrapper.getSentMessages(start, end)

    fun getDeletedMessages(start: LocalDateTime? = null, end: LocalDateTime? = null) = scrapper.getDeletedMessages(start, end)

    fun getMessageRecipients(messageId: Int, loginId: Int = 0) = scrapper.getMessageRecipients(messageId, loginId)

    fun getMessageContent(messageId: Int, folderId: Int, read: Boolean = false, id: Int? = null) = scrapper.getMessageContent(messageId, folderId, read, id)

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>) = scrapper.sendMessage(subject, content, recipients)

    fun deleteMessages(messages: List<Pair<Int, Int>>) = scrapper.deleteMessages(messages)

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null) = scrapper.getTimetable(startDate, endDate)

    fun getCompletedLessons(start: LocalDate, end: LocalDate? = null, subjectId: Int = -1) = scrapper.getCompletedLessons(start, end, subjectId)

    fun getLuckyNumber() = scrapper.getLuckyNumber()
}
