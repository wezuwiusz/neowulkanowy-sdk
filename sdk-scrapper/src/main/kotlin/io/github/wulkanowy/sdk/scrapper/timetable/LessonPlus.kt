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
    val `data`: String,
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
data class LessonPlusChange(
    // @SerialName("dzien")
    // val dzien: Any?,
    // @SerialName("godzinaDo")
    // val godzinaDo: Any?,
    // @SerialName("godzinaOd")
    // val godzinaOd: Any?,
    // @SerialName("grupa")
    // val grupa: Any?,
    @SerialName("informacjeNieobecnosc")
    val informacjeNieobecnosc: String,
    // @SerialName("nrLekcji")
    // val nrLekcji: Any?,
    // @SerialName("prowadzacy")
    // val prowadzacy: Any?,
    // @SerialName("sala")
    // val sala: Any?,
    @SerialName("typProwadzacego")
    val typProwadzacego: Int,
    // @SerialName("zajecia")
    // val zajecia: Any?,
    @SerialName("zmiana")
    val zmiana: Int,
)
