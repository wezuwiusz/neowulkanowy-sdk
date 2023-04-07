package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.sdk.scrapper.register.Student as ScrapperStudent

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
