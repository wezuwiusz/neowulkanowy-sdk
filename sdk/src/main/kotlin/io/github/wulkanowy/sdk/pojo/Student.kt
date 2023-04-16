package io.github.wulkanowy.sdk.pojo

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.scrapper.Scrapper

data class RegisterUser(
    val email: String,
    val login: String, // may be the same as email
    val scrapperBaseUrl: String?,
    val loginType: Scrapper.LoginType?,
    val loginMode: Sdk.Mode,
    val symbols: List<RegisterSymbol>,
)

data class RegisterSymbol(
    val symbol: String,
    val error: Throwable?,
    val userName: String,
    val keyId: String?,
    val privatePem: String?,
    val hebeBaseUrl: String?,
    val schools: List<RegisterUnit>,
)

data class RegisterUnit(
    val userLoginId: Int,
    val schoolId: String,
    val schoolName: String,
    val schoolShortName: String,
    val parentIds: List<Int>,
    val studentIds: List<Int>,
    val employeeIds: List<Int>,
    val error: Throwable?,
    val subjects: List<RegisterSubject>,
)

sealed interface RegisterSubject

data class RegisterEmployee(
    val employeeId: Int,
    val employeeName: String,
) : RegisterSubject

data class RegisterStudent(
    val studentId: Int,
    val studentName: String,
    val studentSecondName: String,
    val studentSurname: String,
    val className: String,
    val classId: Int,
    val isParent: Boolean,
    val semesters: List<Semester>,
) : RegisterSubject
