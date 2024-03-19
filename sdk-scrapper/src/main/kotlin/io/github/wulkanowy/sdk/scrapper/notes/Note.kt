package io.github.wulkanowy.sdk.scrapper.notes

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames
import java.time.LocalDateTime

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Note(

    @SerialName("DataWpisu")
    @JsonNames("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("Nauczyciel")
    @JsonNames("autor")
    val teacher: String,

    @SerialName("Kategoria")
    @JsonNames("kategoria")
    val category: String? = null,

    @SerialName("TrescUwagi")
    @JsonNames("tresc")
    val content: String,

    @SerialName("Punkty")
    val points: String = "",

    @SerialName("PokazPunkty")
    val showPoints: Boolean = false,

    @SerialName("KategoriaTyp")
    @JsonNames("typ")
    val categoryType: Int? = 0,
) {

    @Transient
    lateinit var teacherSymbol: String
}
