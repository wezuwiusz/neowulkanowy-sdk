package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class GradesResponse(

    @SerialName("IsSrednia")
    val isAverage: Boolean = false,

    @SerialName("IsPunkty")
    val isPoints: Boolean = false,

    @SerialName("IsDlaDoroslych")
    val isForAdults: Boolean = false,

    @SerialName("TypOcen")
    val type: Int = 1,

    @SerialName("Oceny")
    @JsonNames("ocenyPrzedmioty")
    val gradesWithSubjects: List<GradeSubject>,

    @SerialName("OcenyOpisowe")
    val gradesDescriptive: List<GradeDescriptive> = emptyList(),

    @SerialName("ustawienia")
    val settings: GradesSettings? = null,
)

@Serializable
internal data class GradesSettings(

    @SerialName("isSrednia")
    val isAverage: Boolean = false,

    @SerialName("isPunkty")
    val isPoints: Boolean = false,

    @SerialName("isDorosli")
    val isForAdults: Boolean = false,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class GradeSubject(

    @SerialName("WidocznyPrzedmiot")
    val visibleSubject: Boolean = false,

    @SerialName("Pozycja")
    @JsonNames("pozycja")
    val order: Int = 0,

    @SerialName("Przedmiot")
    @JsonNames("przedmiotNazwa")
    val name: String = "",

    @SerialName("Srednia")
    @JsonNames("srednia")
    val average: Double = .0,

    @SerialName("SredniaWszystkieSemestry")
    @JsonNames("sredniaWszystkieSemestry")
    val averageAllYear: Double? = null,

    @SerialName("ProponowanaOcenaRoczna")
    @JsonNames("proponowanaOcenaOkresowa")
    val proposed: String? = "",

    @SerialName("OcenaRoczna")
    @JsonNames("ocenaOkresowa")
    val annual: String? = "",

    @SerialName("SumaPunktow")
    @JsonNames("sumaPunktow")
    val pointsSum: String? = "",

    @SerialName("SumaPunktowWszystkieSemestry")
    @JsonNames("SumaPunktowWszystkieSemestry")
    val pointsSumAllYear: String? = "",

    @SerialName("ProponowanaOcenaRocznaPunkty")
    @JsonNames("proponowanaOcenaOkresowaPunkty")
    val proposedPoints: String? = "",

    @SerialName("OcenaRocznaPunkty")
    @JsonNames("ocenaOkresowaPunkty")
    val finalPoints: String? = "",

    @SerialName("OcenyCzastkowe")
    @JsonNames("ocenyCzastkowe")
    val grades: List<Grade>? = emptyList(),
)

@Serializable
data class GradeDescriptive(

    @SerialName("IsReligiaEtyka")
    val isEthics: Boolean,

    @SerialName("NazwaPrzedmiotu")
    val subject: String,

    @SerialName("Opis")
    val description: String? = "",
)
