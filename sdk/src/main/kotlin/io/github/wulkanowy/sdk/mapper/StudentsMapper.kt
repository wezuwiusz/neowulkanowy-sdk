package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.sdk.scrapper.register.Student as ScrapperStudent
import io.github.wulkanowy.sdk.mobile.register.Student as ApiStudent

fun List<ApiStudent>.mapStudents(symbol: String): List<Student> {
    return map {
        Student(
            email = it.userLogin,
            isParent = it.userRole != "uczeń",
            symbol = symbol,
            studentId = it.id,
            userLoginId = it.userLoginId,
            classId = it.classId,
            className = it.classCode.orEmpty(),
            studentName = "${it.name} ${it.surname}",
            schoolSymbol = it.reportingUnitSymbol,
            schoolShortName = it.reportingUnitShortcut,
            schoolName = it.reportingUnitName,
            loginType = Sdk.ScrapperLoginType.STANDARD,
            loginMode = Sdk.Mode.API,
            scrapperBaseUrl = "",
            mobileBaseUrl = it.mobileBaseUrl,
            privateKey = it.privateKey,
            certificateKey = it.certificateKey
        )
    }
}

fun List<ScrapperStudent>.mapStudents(): List<Student> {
    return map {
        Student(
            email = it.email,
            isParent = it.isParent,
            className = it.className,
            classId = it.classId,
            studentId = it.studentId,
            userLoginId = 0,
            symbol = it.symbol,
            loginType = Sdk.ScrapperLoginType.valueOf(it.loginType.name),
            schoolName = it.schoolName,
            schoolShortName = it.schoolShortName,
            schoolSymbol = it.schoolSymbol,
            studentName = it.studentName,
            loginMode = Sdk.Mode.SCRAPPER,
            scrapperBaseUrl = it.baseUrl,
            mobileBaseUrl = "",
            certificateKey = "",
            privateKey = ""
        )
    }
}
