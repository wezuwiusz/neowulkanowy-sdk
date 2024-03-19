package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class LessonPlus(
    @SerialName("adnotacja")
    val adnotacja: Int,
    @SerialName("data")
    val date: String,
    @SerialName("dodatkowe")
    val dodatkowe: Boolean,
    @SerialName("godzinaDo")
    @Serializable(with = CustomDateAdapter::class)
    val godzinaDo: LocalDateTime,
    @SerialName("godzinaOd")
    @Serializable(with = CustomDateAdapter::class)
    val godzinaOd: LocalDateTime,
    @SerialName("podzial")
    val podzial: String?,
    @SerialName("prowadzacy")
    val prowadzacy: String,
    @SerialName("prowadzacyWspomagajacy1")
    val prowadzacyWspomagajacy1: String?,
    @SerialName("prowadzacyWspomagajacy2")
    val prowadzacyWspomagajacy2: String?,
    @SerialName("przedmiot")
    val przedmiot: String,
    // @SerialName("pseudonim")
    // val pseudonim: Any?,
    @SerialName("sala")
    val sala: String,
    @SerialName("zmiany")
    val zmiany: List<LessonPlusChange>,
    @SerialName("zrealizowane")
    val zrealizowane: Boolean,
)

@Serializable
internal data class LessonPlusChange(
    @SerialName("dzien")
    @Serializable(with = CustomDateAdapter::class)
    val dzien: LocalDateTime?,
    @SerialName("godzinaDo")
    @Serializable(with = CustomDateAdapter::class)
    val godzinaDo: LocalDateTime?,
    @SerialName("godzinaOd")
    @Serializable(with = CustomDateAdapter::class)
    val godzinaOd: LocalDateTime?,
    @SerialName("grupa")
    val grupa: String?,
    @SerialName("informacjeNieobecnosc")
    val informacjeNieobecnosc: String?,
    // @SerialName("nrLekcji")
    // val nrLekcji: Any?,
    @SerialName("prowadzacy")
    val prowadzacy: String?,
    @SerialName("sala")
    val sala: String?,
    @SerialName("typProwadzacego")
    val typProwadzacego: Int,
    @SerialName("zajecia")
    val zajecia: String?,
    @SerialName("zmiana")
    val zmiana: Int,
)
