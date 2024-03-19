package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.adapter.GradeDateDeserializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames
import java.time.LocalDate

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Grade(

    @SerialName("Wpis")
    @JsonNames("wpis")
    val entry: String = "",

    @SerialName("KolorOceny") // dec
    @JsonNames("kolorOceny")
    val color: Int = -1,

    @SerialName("KodKolumny")
    val symbol: String? = "",

    @SerialName("NazwaKolumny")
    @JsonNames("nazwaKolumn")
    val description: String? = "",

    @SerialName("Waga")
    @JsonNames("waga")
    val weightValue: Double = .0,

    @SerialName("DataOceny")
    @JsonNames("dataOceny")
    @Serializable(with = GradeDateDeserializer::class)
    internal val privateDate: LocalDate,

    @SerialName("Nauczyciel")
    @JsonNames("nauczyciel")
    val teacher: String = "",
) {

    @Transient
    var subject: String = ""

    @Transient
    var value: Int = 0

    @Transient
    var modifier: Double = .0

    @Transient
    var comment: String = ""

    @Transient
    var weight: String = ""

    @Transient
    var colorHex: String = ""

    @Transient
    lateinit var date: LocalDate
}
