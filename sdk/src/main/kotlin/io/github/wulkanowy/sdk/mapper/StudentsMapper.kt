package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.sdk.mobile.register.Student as ApiStudent
import io.github.wulkanowy.sdk.scrapper.register.Student as ScrapperStudent

fun List<ApiStudent>.mapStudents(symbol: String) = map {
    Student(
        email = it.userLogin,
        userName = it.userName,
        userLogin = it.userLogin,
        userLoginId = it.userLoginId,
        isParent = it.userRole != "uczeń",
        symbol = symbol,
        studentId = it.id,
        classId = it.classId,
        className = it.classCode.orEmpty(),
        studentName = it.name,
        studentSurname = it.surname,
        schoolSymbol = it.reportingUnitSymbol,
        schoolShortName = it.reportingUnitShortcut,
        schoolName = it.reportingUnitName,
        loginType = Sdk.ScrapperLoginType.STANDARD,
        loginMode = Sdk.Mode.API,
        scrapperBaseUrl = "",
        mobileBaseUrl = it.mobileBaseUrl,
        privateKey = it.privateKey,
        certificateKey = it.certificateKey,
        semesters = mapSemesters(it.id),
    )
}

fun List<ScrapperStudent>.mapStudents() = map {
    Student(
        email = it.email,
        userName = it.userName,
        userLogin = it.userLogin,
        userLoginId = it.userLoginId,
        isParent = it.isParent,
        className = it.className,
        classId = it.classId,
        studentId = it.studentId,
        symbol = it.symbol,
        loginType = Sdk.ScrapperLoginType.valueOf(it.loginType.name),
        schoolName = it.schoolName,
        schoolShortName = it.schoolShortName,
        schoolSymbol = it.schoolSymbol,
        studentName = it.studentName,
        studentSurname = it.studentSurname,
        loginMode = Sdk.Mode.SCRAPPER,
        scrapperBaseUrl = it.baseUrl,
        mobileBaseUrl = "",
        certificateKey = "",
        privateKey = "",
        semesters = it.semesters.mapSemesters(),
    )
}
