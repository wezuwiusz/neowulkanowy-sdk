package io.github.wulkanowy.sdk.scrapper.notes

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDateTime

@Serializable
data class Note(

    @SerialName("DataWpisu")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("Nauczyciel")
    val teacher: String,

    @SerialName("Kategoria")
    val category: String,

    @SerialName("TrescUwagi")
    val content: String,

    @SerialName("Punkty")
    val points: String = "",

    @SerialName("PokazPunkty")
    val showPoints: Boolean = false,

    @SerialName("KategoriaTyp")
    val categoryType: Int = 0,
) {

    @Transient
    lateinit var teacherSymbol: String
}
