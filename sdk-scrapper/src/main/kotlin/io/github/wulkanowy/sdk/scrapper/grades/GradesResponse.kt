package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class GradesResponse(

    @SerialName("ustawienia")
    val settings: GradesSettings? = null,

    @SerialName("IsSrednia")
    val isAverage: Boolean,

    @SerialName("IsPunkty")
    val isPoints: Boolean,

    @SerialName("IsDlaDoroslych")
    val isForAdults: Boolean,

    @SerialName("TypOcen")
    val type: Int = 1,

    @SerialName("Oceny")
    @JsonNames("ocenyPrzedmioty")
    val gradesWithSubjects: List<GradeSubject>,

    @SerialName("OcenyOpisowe")
    val gradesDescriptive: List<GradeDescriptive> = emptyList(),
)

@Serializable
internal data class GradesSettings(

    @SerialName("isSrednia")
    val isAverage: Boolean,

    @SerialName("isPunkty")
    val isPoints: Boolean,

    @SerialName("isDlaDoroslych")
    val isForAdults: Boolean,
)

@Serializable
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

    @SerialName("ProponowanaOcenaRoczna")
    @JsonNames("proponowanaOcenaOkresowa")
    val proposed: String? = "",

    @SerialName("OcenaRoczna")
    @JsonNames("ocenaOkresowa")
    val annual: String? = "",

    @SerialName("SumaPunktow")
    @JsonNames("sumaPunktow")
    val pointsSum: String? = "",

    @SerialName("ProponowanaOcenaRocznaPunkty")
    @JsonNames("proponowanaOcenaOkresowaPunkty")
    val proposedPoints: String? = "",

    @SerialName("OcenaRocznaPunkty")
    @JsonNames("ocenaOkresowaPunkty")
    val finalPoints: String? = "",

    @SerialName("OcenyCzastkowe")
    @JsonNames("ocenyCzastkowe")
    val grades: List<Grade> = emptyList(),
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
