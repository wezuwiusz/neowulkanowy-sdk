package io.github.wulkanowy.sdk.scrapper.student

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import io.github.wulkanowy.sdk.scrapper.adapter.GradeDateDeserializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class StudentInfo(

    @SerialName("Imie")
    @JsonNames("imie")
    val name: String,

    @SerialName("Imie2")
    @JsonNames("imie2")
    val middleName: String?,

    // @SerialName("NumerDokumentu")
    // @JsonNames("numerDokumentu")
    // val idNumber: Any?,

    @SerialName("Nazwisko")
    @JsonNames("nazwisko")
    val lastName: String,

    @SerialName("DataUrodzenia")
    @Serializable(with = CustomDateAdapter::class)
    val birthDate: LocalDateTime?,

    @JsonNames("dataUrodzenia")
    @Serializable(with = GradeDateDeserializer::class)
    internal val birthDateEduOne: LocalDate?,

    @SerialName("MiejsceUrodzenia")
    @JsonNames("miejsceUrodzenia")
    val birthPlace: String?,

    @SerialName("NazwiskoRodowe")
    @JsonNames("nazwiskoRodowe")
    val familyName: String?,

    @SerialName("ObywatelstwoPolskie")
    @JsonNames("obywatelstwoPolskie")
    val polishCitizenship: Int,

    @SerialName("ImieMatki")
    @JsonNames("imieMatki")
    val motherName: String?,

    @SerialName("ImieOjca")
    @JsonNames("imieOjca")
    val fatherName: String?,

    @SerialName("Plec")
    @JsonNames("plec")
    val gender: Boolean,

    @SerialName("AdresZamieszkania")
    @JsonNames("adresZamieszkania")
    val address: String,

    @SerialName("AdresZameldowania")
    @JsonNames("adresZameldowania")
    val registeredAddress: String,

    @SerialName("AdresKorespondencji")
    @JsonNames("adresKorespondencji")
    val correspondenceAddress: String,

    @SerialName("TelDomowy")
    @JsonNames("telDomowy")
    val homePhone: String?,

    @SerialName("TelKomorkowy")
    @JsonNames("telKomorkowy")
    val cellPhone: String?,

    @SerialName("Email")
    @JsonNames("email")
    val email: String?,

    @SerialName("CzyWidocznyPesel")
    val isVisiblePesel: Boolean = false,

    @SerialName("Opiekun1")
    @JsonNames("opiekun1")
    val guardianFirst: StudentGuardian?,

    @SerialName("Opiekun2")
    @JsonNames("opiekun2")
    val guardianSecond: StudentGuardian?,

    @SerialName("UkryteDaneAdresowe")
    @JsonNames("ukryteDaneAdresowe")
    val hideAddress: Boolean,

    @SerialName("ImieNazwisko")
    val fullName: String?,

    @SerialName("PosiadaPesel")
    val hasPesel: Boolean?,

    @SerialName("Polak")
    val isPole: Boolean?,

    @SerialName("ImieMatkiIOjca")
    val motherAndFatherNames: String?,
)
