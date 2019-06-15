package io.github.wulkanowy.sdk.register

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.api.register.Student as ScrapperStudent
import io.github.wulkanowy.sdk.register.Student as ApiStudent

fun List<ApiStudent>.mapStudents(symbol: String, certificateResponse: CertificateResponse): List<Student> {
    return map {
        Student(
            email = it.userLogin,
            symbol = symbol,
            studentId = it.id,
            userLoginId = it.userLoginId,
            classId = it.classId,
            className = it.classCode.orEmpty(),
            studentName = "${it.name} ${it.surname}",
            schoolSymbol = it.reportingUnitSymbol,
            schoolName = it.reportingUnitName,
            loginType = Api.LoginType.STANDARD,
            loginMode = Sdk.Mode.API,
            apiHost = certificateResponse.tokenCert!!.apiEndpoint.removeSuffix("/"),
            scrapperHost = "",
            ssl = certificateResponse.tokenCert.apiEndpoint.startsWith("https"),
            certificate = certificateResponse.tokenCert.certificatePfx,
            certificateKey = certificateResponse.tokenCert.certificateKey
        )
    }
}

fun List<ScrapperStudent>.mapStudents(ssl: Boolean, scrapperHost: String): List<Student> {
    return map {
        Student(
            email = it.email,
            className = it.className,
            classId = it.classId,
            studentId = it.studentId,
            userLoginId = 0,
            symbol = it.symbol,
            loginType = it.loginType,
            schoolName = it.schoolName,
            schoolSymbol = it.schoolSymbol,
            studentName = it.studentName,
            loginMode = Sdk.Mode.SCRAPPER,
            ssl = ssl,
            apiHost = "",
            scrapperHost = scrapperHost,
            certificateKey = "",
            certificate = ""
        )
    }
}
