package io.github.wulkanowy.api

import io.github.wulkanowy.api.auth.NotLoggedInException
import io.github.wulkanowy.api.repository.*
import io.github.wulkanowy.api.service.ServiceManager
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*

class Api {

    var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC

    var ssl: Boolean = true

    var host: String = "fakelog.cf"

    var symbol: String = "Default"

    var email: String = ""

    var password: String = ""

    var schoolId: String = ""

    var studentId: String = ""

    var diaryId: String = ""

    private val changeManager = resettableManager()

    fun notifyDataChanged() = changeManager.reset()

    private val schema by resettableLazy(changeManager) { "http" + if (ssl) "s" else "" }

    private val serviceManager by resettableLazy(changeManager) {
        ServiceManager(logLevel, schema, host, symbol, email, password, schoolId, studentId, diaryId)
    }

    private val register by resettableLazy(changeManager) {
        RegisterRepository(symbol, email, password,
                LoginRepository(schema, host, symbol, serviceManager.getLoginService()),
                serviceManager.getSnpService(false, false)
        )
    }

    private val snpStart by resettableLazy(changeManager) {
        if (studentId.isBlank()) throw NotLoggedInException("Student id is not set")
        StudentAndParentStartRepository(symbol, schoolId, studentId, serviceManager.getSnpService(true, false))
    }

    private val snp by resettableLazy(changeManager) {
        StudentAndParentRepository(serviceManager.getSnpService())
    }

    private val messages by resettableLazy(changeManager) {
        MessagesRepository(studentId.toInt(), serviceManager.getMessagesService())
    }

    fun getPupils() = register.getPupils()

    fun getSemesters() = snpStart.getSemesters()

    fun getSchoolInfo() = snp.getSchoolInfo()

    fun getAttendance(startDate: Date? = null) = snp.getAttendance(startDate)

    fun getExams(startDate: Date? = null) = snp.getExams(startDate)

    fun getGrades(semesterId: Int? = null) = snp.getGrades(semesterId)

    fun getGradesSummary(semesterId: Int? = null) = snp.getGradesSummary(semesterId)

    fun getHomework(date: Date? = null) = snp.getHomework(date)

    fun getNotes() = snp.getNotes()

    fun getRegisteredDevices() = snp.getRegisteredDevices()

    fun getToken() = snp.getToken()

    fun unregisterDevice(id: Int) = snp.unregisterDevice(id)

    fun getTeachers() = snp.getTeachers()

    fun getStudentInfo() = snp.getStudentInfo()

    fun getReportingUnits() = messages.getReportingUnits()

    fun getRecipients(role: Int = 2) = messages.getRecipients(role)

    fun getReceivedMessages(endDate: Date? = null, startDate: Date? = null) = messages.getReceivedMessages(startDate, endDate)

    fun getSentMessages(endDate: Date? = null, startDate: Date? = null) = messages.getSentMessages(startDate, endDate)

    fun getDeletedMessages(startDate: Date? = null, endDate: Date? = null) = messages.getDeletedMessages(startDate, endDate)

    fun getMessage(id: Int, folderId: Int) = messages.getMessage(id, folderId)
}
