package io.github.wulkanowy.sdk

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.api.resettableLazy
import io.github.wulkanowy.api.resettableManager
import io.github.wulkanowy.sdk.pojo.Student
import io.github.wulkanowy.sdk.repository.MobileRepository
import io.github.wulkanowy.sdk.repository.RegisterRepository
import io.reactivex.Observable
import io.reactivex.Single
import java.time.Instant
import java.util.*

class Sdk {

    enum class Mode {
        API,
        SCRAPPER,
        HYBRID
    }

    var mode = Mode.HYBRID

    var apiHost = "https://api.fakelog.cf"
    var ssl = true
    var scrapperHost = "fakelog.cf"
    var signature = ""
    var certificate = ""

    var email = ""
    var password = ""

    var schoolSymbol = ""
    var classId = 0
    var studentId = 0

    var apiKey = ""
    var pin = ""
    var token = ""
    var symbol = ""
    var deviceName = "Wulkanowy SDK"

    private val scrapper = Api()

    private val resettableManager = resettableManager()

    private val mobile by resettableLazy(resettableManager) {
        MobileRepository(apiKey, apiHost, symbol, signature, certificate, schoolSymbol)
    }

    private fun getDictionaries() = mobile.getDictionaries(0, 0, 0)

    fun getStudents(): Single<List<Student>> {
        return when (mode) {
            Mode.API -> getApiStudents(token, pin, symbol)
            Mode.SCRAPPER -> {
                scrapper.run {
                    ssl = this@Sdk.ssl
                    host = this@Sdk.scrapperHost
                    email = this@Sdk.email
                    password = this@Sdk.password
                    getStudents().map { students ->
                        students.map {
                            Student(
                                    email = it.email,
                                    className = it.className,
                                    classId = it.classId,
                                    studentId = it.studentId,
                                    symbol = it.symbol,
                                    loginType = it.loginType,
                                    schoolName = it.schoolName,
                                    schoolSymbol = it.schoolSymbol,
                                    studentName = it.studentName,
                                    loginMode = Mode.SCRAPPER,
                                    ssl = ssl,
                                    apiHost = "",
                                    scrapperHost = scrapperHost,
                                    certificateKey = "",
                                    certificate = ""
                            )
                        }
                    }
                }
            }
            Mode.HYBRID -> {
                scrapper.run {
                    ssl = this@Sdk.ssl
                    host = this@Sdk.scrapperHost
                    email = this@Sdk.email
                    password = this@Sdk.password
                    getStudents().flatMapObservable { Observable.fromIterable(it) }.flatMapSingle {
                        scrapper.run {
                            symbol = it.symbol
                            schoolSymbol = it.schoolSymbol
                            studentId = it.studentId
                            diaryId = -1
                            classId = it.classId
                            loginType = it.loginType
                        }
                        scrapper.getToken().flatMap {
                            getApiStudents(it.token, it.pin, it.symbol)
                        }.map { apiStudents ->
                            apiStudents.map { apiStudent ->
                                apiStudent.copy(loginMode = Mode.HYBRID, scrapperHost = scrapperHost)
                            }
                        }
                    }.toList().map { it.flatten() }
                }
            }
        }
    }

    private fun getApiStudents(token: String, pin: String, symbol: String): Single<List<Student>> {
        return RegisterRepository(apiKey, apiHost).run {
            getRouteByToken(token).flatMap {
                baseHost = it
                this.symbol = symbol
                getCertificate(token, pin, deviceName)
            }.flatMap { certificateResponse ->
                if (certificateResponse.isError) throw RuntimeException(certificateResponse.message)
                certKey = certificateResponse.tokenCert!!.certificateKey
                certificate = certificateResponse.tokenCert.certificatePfx
                getPupils().map { students ->
                    students.map {
                        Student(
                            email = it.userLogin,
                            symbol = symbol,
                            studentId = it.id,
                            classId = it.classId,
                            className = it.classCode,
                            studentName = it.userName,
                            schoolSymbol = it.reportingUnitSymbol,
                            schoolName = it.reportingUnitName,
                            loginType = Api.LoginType.STANDARD,
                            loginMode = Mode.API,
                            apiHost = certificateResponse.tokenCert.apiEndpoint,
                            scrapperHost = "",
                            ssl = certificateResponse.tokenCert.apiEndpoint.startsWith("https"),
                            certificate = certificateResponse.tokenCert.certificatePfx,
                            certificateKey = certificateResponse.tokenCert.certificateKey
                        )
                    }
                }
            }
        }
    }

    fun getGrades(semesterId: Int): Single<List<Grade>> {
        return when (mode) {
            Mode.API -> getApiGrades(semesterId)
            Mode.SCRAPPER -> scrapper.getGrades(semesterId).map { grades ->
                grades.map {
                    Grade(
                        subject = it.subject,
                        description = it.description ?: "",
                        symbol = it.symbol,
                        comment = it.comment,
                        date = it.date,
                        teacher = it.teacher,
                        entry = it.entry,
                        weight = it.weight,
                        weightValue = it.weightValue,
                        color = it.color,
                        value = it.value.toDouble(),
                        modifier = it.modifier
                    )
                }
            }
            Mode.HYBRID -> getApiGrades(semesterId)
        }
    }

    private fun getApiGrades(semesterId: Int): Single<List<Grade>> {
        return getDictionaries().flatMap { dict ->
            mobile.getGrades(classId, semesterId, studentId).map { grades ->
                grades.map { grade ->
                    Grade(
                        subject = dict.subjects.singleOrNull { it.id == grade.subjectId }?.name ?: "",
                        description = dict.gradeCategories.singleOrNull { it.id == grade.categoryId }?.name ?: "",
                        symbol = dict.gradeCategories.singleOrNull { it.id == grade.categoryId }?.code ?: "",
                        comment = grade.comment,
                        date = Date.from(Instant.ofEpochSecond(grade.creationDate.toLong())),
                        teacher = dict.teachers.singleOrNull { it.id == grade.employeeIdD }?.let { "${it.name} ${it.surname}" } ?: "",
                        entry = grade.entry,
                        weightValue = grade.gradeWeight,
                        modifier = grade.modificationWeight ?: .0,
                        value = grade.value,
                        weight = grade.weight,
                        color = "0"
                    )
                }
            }
        }
    }
}
