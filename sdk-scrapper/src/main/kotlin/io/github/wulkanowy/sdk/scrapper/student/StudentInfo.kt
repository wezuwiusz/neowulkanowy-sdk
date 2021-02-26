package io.github.wulkanowy.sdk.scrapper.student

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class StudentInfo(

    @Json(name = "Imie")
    val name: String,

    @Json(name = "Imie2")
    val middleName: String?,

    @Json(name = "NumerDokumentu")
    val idNumber: Any?,

    @Json(name = "Nazwisko")
    val lastName: String,

    @Json(name = "DataUrodzenia")
    val birthDate: Date,

    @Json(name = "MiejsceUrodzenia")
    val birthPlace: String,

    @Json(name = "NazwiskoRodowe")
    val familyName: String?,

    @Json(name = "ObywatelstwoPolskie")
    val polishCitizenship: Int,

    @Json(name = "ImieMatki")
    val motherName: String?,

    @Json(name = "ImieOjca")
    val fatherName: String?,

    @Json(name = "Plec")
    val gender: Boolean,

    @Json(name = "AdresZamieszkania")
    val address: String,

    @Json(name = "AdresZameldowania")
    val registeredAddress: String,

    @Json(name = "AdresKorespondencji")
    val correspondenceAddress: String,

    @Json(name = "TelDomowy")
    val homePhone: String?,

    @Json(name = "TelKomorkowy")
    val cellPhone: String?,

    @Json(name = "Email")
    val email: String?,

    @Json(name = "CzyWidocznyPesel")
    val isVisiblePesel: Boolean,

    @Json(name = "Opiekun1")
    val guardianFirst: StudentGuardian?,

    @Json(name = "Opiekun2")
    val guardianSecond: StudentGuardian?,

    @Json(name = "UkryteDaneAdresowe")
    val hideAddress: Boolean,

    @Json(name = "ImieNazwisko")
    val fullName: String,

    @Json(name = "PosiadaPesel")
    val hasPesel: Boolean,

    @Json(name = "Polak")
    val isPole: Boolean,

    @Json(name = "ImieMatkiIOjca")
    val motherAndFatherNames: String?
)
