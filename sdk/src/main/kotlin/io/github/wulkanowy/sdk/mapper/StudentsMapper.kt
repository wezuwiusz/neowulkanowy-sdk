package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.hebe.register.StudentInfo
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

fun List<StudentInfo>.mapHebeStudents(certificateKey: String, privateKey: String): List<Student> {
    return map {
        Student(
            email = it.pupil.loginValue,
            isParent = it.login.loginRole != "Uczen",
            className = it.classDisplay,
            classId = -1,
            studentId = it.pupil.id,
            userLoginId = it.pupil.loginId,
            symbol = it.topLevelPartition,
            loginType = Sdk.ScrapperLoginType.STANDARD,
            schoolName = it.constituentUnit.name,
            schoolShortName = it.constituentUnit.short,
            schoolSymbol = it.unit.symbol,
            studentName = it.pupil.let { pupil -> "${pupil.firstName} ${pupil.surname}" },
            loginMode = Sdk.Mode.HEBE,
            scrapperBaseUrl = "",
            mobileBaseUrl = it.unit.restUrl,
            certificateKey = certificateKey,
            privateKey = privateKey
        )
    }
}
