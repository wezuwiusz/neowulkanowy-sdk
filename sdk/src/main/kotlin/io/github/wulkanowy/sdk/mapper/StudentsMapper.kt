package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.RegisterEmployee
import io.github.wulkanowy.sdk.pojo.RegisterStudent
import io.github.wulkanowy.sdk.pojo.RegisterSubject
import io.github.wulkanowy.sdk.pojo.RegisterSymbol
import io.github.wulkanowy.sdk.pojo.RegisterUnit
import io.github.wulkanowy.sdk.pojo.RegisterUser
import io.github.wulkanowy.sdk.scrapper.register.RegisterEmployee as ScrapperRegisterEmploye
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent as ScrapperRegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.RegisterSubject as ScrapperRegisterSubject
import io.github.wulkanowy.sdk.scrapper.register.RegisterSymbol as SdkRegisterSymbol
import io.github.wulkanowy.sdk.scrapper.register.RegisterUnit as ScrapperRegisterUnit
import io.github.wulkanowy.sdk.scrapper.register.RegisterUser as ScrapperRegisterUser

internal fun ScrapperRegisterUser.mapUser(): RegisterUser = RegisterUser(
    email = email,
    login = login,
    baseUrl = baseUrl,
    loginType = loginType,
    symbols = symbols.map { it.mapSymbol() },
)

internal fun SdkRegisterSymbol.mapSymbol(): RegisterSymbol = RegisterSymbol(
    symbol = symbol,
    userName = userName,
    error = error,
    schools = schools.map { it.mapUnit() },
)

internal fun ScrapperRegisterUnit.mapUnit(): RegisterUnit = RegisterUnit(
    userLoginId = userLoginId,
    schoolId = schoolId,
    schoolName = schoolName,
    schoolShortName = schoolShortName,
    parentIds = parentIds,
    studentIds = studentIds,
    employeeIds = employeeIds,
    error = error,
    subjects = subjects.map { it.mapSubject() },
)

internal fun ScrapperRegisterSubject.mapSubject(): RegisterSubject {
    return when (this) {
        is ScrapperRegisterStudent -> mapStudent()
        is ScrapperRegisterEmploye -> mapEmployee()
    }
}

internal fun ScrapperRegisterEmploye.mapEmployee(): RegisterEmployee = RegisterEmployee(
    employeeId = employeeId,
    employeeName = employeeName,
)

internal fun ScrapperRegisterStudent.mapStudent(): RegisterStudent = RegisterStudent(
    studentId = studentId,
    studentName = studentName,
    studentSecondName = studentSecondName,
    studentSurname = studentSurname,
    className = className,
    classId = classId,
    isParent = isParent,
    semesters = semesters.mapSemesters(),
)
