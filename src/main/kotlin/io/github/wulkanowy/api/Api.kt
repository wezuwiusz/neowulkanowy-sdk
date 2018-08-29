package io.github.wulkanowy.api

import io.github.wulkanowy.api.auth.NotLoggedInException
import io.github.wulkanowy.api.repository.LoginRepository
import io.github.wulkanowy.api.repository.RegisterRepository
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.github.wulkanowy.api.repository.StudentAndParentStartRepository
import io.github.wulkanowy.api.service.ServiceManager
import okhttp3.logging.HttpLoggingInterceptor

class Api {

    var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC

    var holdSession: Boolean = true

    var ssl: Boolean = true

    var host: String = "fakelog.cf"

    var symbol: String = "Default"

    var email: String = ""

    var password: String = ""

    var schoolId: String = ""

    var studentId: String = ""

    var diaryId: String = ""

    private val schema by lazy { "http" + if (ssl) "s" else "" }

    private val applyChanges = resettableManager()

    fun notifyDataChanged() = applyChanges.reset()

    private val serviceManager by resettableLazy(applyChanges) {
        ServiceManager(logLevel, holdSession, schema, host, symbol, email, password, schoolId, studentId, diaryId)
    }

    private val register by resettableLazy(applyChanges) {
        RegisterRepository(symbol, email, password,
                LoginRepository(schema, host, symbol, serviceManager.getLoginService()),
                serviceManager.getSnpService(false, false)
        )
    }

    private val snpStart by resettableLazy(applyChanges) {
        if (studentId.isBlank()) throw NotLoggedInException("Student id is not set")
        StudentAndParentStartRepository(symbol, schoolId, studentId, serviceManager.getSnpService(true, false))
    }

    private val snp by resettableLazy(applyChanges) {
        StudentAndParentRepository(serviceManager.getSnpService())
    }

    fun getPupils() = register.getPupils()

    fun getSemesters() = snpStart.getSemesters()

    fun getSchoolInfo() = snp.getSchoolInfo()

    fun getAttendance(startDate: String) = snp.getAttendance(startDate)

    fun getExams(startDate: String) = snp.getExams(startDate)

    fun getGrades(semester: Int) = snp.getGrades(semester)

    fun getGradesSummary(semester: Int) = snp.getGradesSummary(semester)

    fun getHomework(date: String) = snp.getHomework(date)

    fun getNotes() = snp.getNotes()

    fun getTeachers() = snp.getTeachers()

    fun getStudentInfo() = snp.getStudentInfo()
}
