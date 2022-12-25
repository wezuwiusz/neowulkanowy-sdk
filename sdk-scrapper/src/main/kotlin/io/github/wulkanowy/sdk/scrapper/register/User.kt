package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.Scrapper

data class RegisterUser(
    val email: String,
    val login: String, // may be the same as email
    val baseUrl: String,
    val loginType: Scrapper.LoginType,
    val symbols: List<RegisterSymbol>,
)

data class RegisterSymbol(
    val symbol: String,
    val error: Throwable?,
    val userName: String,
    val schools: List<RegisterUnit>,
)

/**
 * @see [PermissionUnit]
 */
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

interface RegisterSubject

data class RegisterTeacher(
    val teacherId: Int,
    val teacherName: String,
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
