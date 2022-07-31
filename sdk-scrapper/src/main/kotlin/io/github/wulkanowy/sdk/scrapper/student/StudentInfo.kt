package io.github.wulkanowy.sdk.scrapper.student

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class StudentInfo(

    @SerialName("Imie")
    val name: String,

    @SerialName("Imie2")
    val middleName: String?,

    // @SerialName("NumerDokumentu")
    // val idNumber: Any?,

    @SerialName("Nazwisko")
    val lastName: String,

    @SerialName("DataUrodzenia")
    @Serializable(with = CustomDateAdapter::class)
    val birthDate: LocalDateTime,

    @SerialName("MiejsceUrodzenia")
    val birthPlace: String?,

    @SerialName("NazwiskoRodowe")
    val familyName: String?,

    @SerialName("ObywatelstwoPolskie")
    val polishCitizenship: Int,

    @SerialName("ImieMatki")
    val motherName: String?,

    @SerialName("ImieOjca")
    val fatherName: String?,

    @SerialName("Plec")
    val gender: Boolean,

    @SerialName("AdresZamieszkania")
    val address: String,

    @SerialName("AdresZameldowania")
    val registeredAddress: String,

    @SerialName("AdresKorespondencji")
    val correspondenceAddress: String,

    @SerialName("TelDomowy")
    val homePhone: String?,

    @SerialName("TelKomorkowy")
    val cellPhone: String?,

    @SerialName("Email")
    val email: String?,

    @SerialName("CzyWidocznyPesel")
    val isVisiblePesel: Boolean,

    @SerialName("Opiekun1")
    val guardianFirst: StudentGuardian?,

    @SerialName("Opiekun2")
    val guardianSecond: StudentGuardian?,

    @SerialName("UkryteDaneAdresowe")
    val hideAddress: Boolean,

    @SerialName("ImieNazwisko")
    val fullName: String,

    @SerialName("PosiadaPesel")
    val hasPesel: Boolean,

    @SerialName("Polak")
    val isPole: Boolean,

    @SerialName("ImieMatkiIOjca")
    val motherAndFatherNames: String?
)
