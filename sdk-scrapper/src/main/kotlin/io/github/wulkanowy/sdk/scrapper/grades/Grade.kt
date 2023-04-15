package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.adapter.GradeDateDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDate

@Serializable
data class Grade(

    @SerialName("Wpis")
    val entry: String = "",

    @SerialName("KolorOceny") // dec
    val color: Int = -1,

    @SerialName("KodKolumny")
    val symbol: String? = "",

    @SerialName("NazwaKolumny")
    val description: String? = "",

    @SerialName("Waga")
    val weightValue: Double = .0,

    @SerialName("DataOceny")
    @Serializable(with = GradeDateDeserializer::class)
    internal val privateDate: LocalDate,

    @SerialName("Nauczyciel")
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
