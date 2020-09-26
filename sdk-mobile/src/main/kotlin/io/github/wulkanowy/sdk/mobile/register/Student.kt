package io.github.wulkanowy.sdk.mobile.register

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Student(

    @Json(name = "IdOkresKlasyfikacyjny")
    val classificationPeriodId: Int,

    @Json(name = "OkresPoziom")
    val periodLevel: Int,

    @Json(name = "OkresNumer")
    val periodNumber: Int,

    @Json(name = "OkresDataOd")
    val periodDateFrom: Long,

    @Json(name = "OkresDataDo")
    val periodDateTo: Long,

    @Json(name = "OkresDataOdTekst")
    val periodDateFromText: String,

    @Json(name = "OkresDataDoTekst")
    val periodDateToText: String,

    @Json(name = "IdJednostkaSprawozdawcza")
    val reportingUnitId: Int,

    @Json(name = "JednostkaSprawozdawczaSkrot")
    val reportingUnitShortcut: String,

    @Json(name = "JednostkaSprawozdawczaNazwa")
    val reportingUnitName: String,

    @Json(name = "JednostkaSprawozdawczaSymbol")
    val reportingUnitSymbol: String,

    @Json(name = "IdJednostka")
    val unitId: Int,

    @Json(name = "JednostkaNazwa")
    val unitName: String,

    @Json(name = "JednostkaSkrot")
    val unitShortcut: String,

    @Json(name = "OddzialSymbol")
    val classSymbol: String,

    @Json(name = "OddzialKod")
    val classCode: String?,

    @Json(name = "UzytkownikRola")
    val userRole: String,

    @Json(name = "UzytkownikLogin")
    val userLogin: String,

    @Json(name = "UzytkownikLoginId")
    val userLoginId: Int,

    @Json(name = "UzytkownikNazwa")
    val userName: String,

    @Json(name = "Id")
    val id: Int,

    @Json(name = "IdOddzial")
    val classId: Int,

    @Json(name = "Imie")
    val name: String,

    @Json(name = "Imie2")
    val nameSecond: String?,

    @Json(name = "Nazwisko")
    val surname: String,

    @Json(name = "Pseudonim")
    val nick: String?,

    @Json(name = "UczenPlec")
    val pupilGender: Int,

    @Json(name = "Pozycja")
    val position: Int,

    @Json(name = "LoginId")
    val loginId: Int?
) {

    var privateKey = ""

    var certificateKey = ""

    var mobileBaseUrl = ""
}
