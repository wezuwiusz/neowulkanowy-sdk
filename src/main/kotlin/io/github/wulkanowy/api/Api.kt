package io.github.wulkanowy.api

import io.github.wulkanowy.api.repository.*
import io.github.wulkanowy.api.service.ServiceManager
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.LocalDate

class Api {

    var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC

    var ssl: Boolean = true

    var host: String = "fakelog.cf"

    var loginType: LoginType = LoginType.AUTO

    var symbol: String = "Default"

    var email: String = ""

    var password: String = ""

    var schoolSymbol: String = ""

    var studentId: Int = 0

    var diaryId: Int = 0

    enum class LoginType {
        AUTO,
        STANDARD,
        ADFS,
        ADFSCards,
        ADFSLight
    }

    private val changeManager = resettableManager()

    fun notifyDataChanged() = changeManager.reset()

    private val schema by resettableLazy(changeManager) { "http" + if (ssl) "s" else "" }

    private val normalizedSymbol by resettableLazy(changeManager) { if (symbol.isBlank()) "Default" else symbol }

    private val serviceManager by resettableLazy(changeManager) {
        ServiceManager(logLevel, loginType, schema, host, normalizedSymbol, email, password, schoolSymbol, studentId, diaryId)
    }

    private val register by resettableLazy(changeManager) {
        RegisterRepository(normalizedSymbol, email, password,
                LoginRepository(loginType, schema, host, normalizedSymbol, serviceManager.getLoginService()),
                serviceManager.getRegisterService(),
                serviceManager.getSnpService(false, false)
        )
    }

    private val snpStart by resettableLazy(changeManager) {
        if (0 == studentId) throw ApiException("Student id is not set")
        StudentAndParentStartRepository(normalizedSymbol, schoolSymbol, studentId, serviceManager.getSnpService(true, false))
    }

    private val snp by resettableLazy(changeManager) {
        StudentAndParentRepository(serviceManager.getSnpService())
    }

    private val messages by resettableLazy(changeManager) {
        MessagesRepository(studentId, serviceManager.getMessagesService())
    }

    fun setInterceptor(interceptor: Interceptor, index: Int = -1) {
        serviceManager.setInterceptor(interceptor, index)
    }

    fun getPupils() = register.getPupils()

    fun getSemesters() = snpStart.getSemesters()

    fun getCurrentSemester() = snp.getCurrentSemester()

    fun getAttendance(startDate: LocalDate, endDate: LocalDate? = null) = snp.getAttendance(startDate, endDate)

    fun getAttendanceSummary(subjectId: Int? = null) = snp.getAttendanceSummary(subjectId)

    fun getExams(startDate: LocalDate, endDate: LocalDate? = null) = snp.getExams(startDate, endDate)

    fun getGrades(semesterId: Int? = null) = snp.getGrades(semesterId)

    fun getGradesSummary(semesterId: Int? = null) = snp.getGradesSummary(semesterId)

    fun getGradesStatistics(semesterId: Int? = null, annual: Boolean = false) = snp.getGradesStatistics(semesterId, annual)

    fun getHomework(startDate: LocalDate, endDate: LocalDate? = null) = snp.getHomework(startDate, endDate)

    fun getNotes() = snp.getNotes()

    fun getRegisteredDevices() = snp.getRegisteredDevices()

    fun getToken() = snp.getToken()

    fun unregisterDevice(id: Int) = snp.unregisterDevice(id)

    fun getTeachers() = snp.getTeachers()

    fun getStudentInfo() = snp.getStudentInfo()

    fun getReportingUnits() = messages.getReportingUnits()

    fun getRecipients(role: Int = 2) = messages.getRecipients(role)

    fun getReceivedMessages(endDate: LocalDate? = null, startDate: LocalDate? = null) = messages.getReceivedMessages(startDate, endDate)

    fun getSentMessages(endDate: LocalDate? = null, startDate: LocalDate? = null) = messages.getSentMessages(startDate, endDate)

    fun getDeletedMessages(startDate: LocalDate? = null, endDate: LocalDate? = null) = messages.getDeletedMessages(startDate, endDate)

    fun getMessage(id: Int, folderId: Int) = messages.getMessage(id, folderId)

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null) = snp.getTimetable(startDate, endDate)

    fun getRealized(startDate: LocalDate? = null) = snp.getRealized(startDate)
}
