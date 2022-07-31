package io.github.wulkanowy.sdk.scrapper.exams

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDateTime

@Serializable
data class Exam(

    @SerialName("DataModyfikacji")
    @Serializable(with = CustomDateAdapter::class)
    val entryDate: LocalDateTime,

    @SerialName("Nazwa")
    val subject: String,

    @SerialName("Rodzaj")
    val type: Int,

    @SerialName("Opis")
    val description: String,

    @SerialName("Pracownik")
    val teacher: String
) {

    @Transient
    lateinit var typeName: String

    @Transient
    lateinit var date: LocalDateTime

    @Transient
    lateinit var teacherSymbol: String
}
