package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Note(
    @SerialName("Id")
    val id: Int,
    @SerialName("Key")
    val key: String,
    @SerialName("IdPupil")
    val idPupil: Int,
    @SerialName("Positive")
    val positive: Boolean,
    @SerialName("Content")
    val content: String,
    @SerialName("Points")
    val points: Int?,
    @SerialName("DateValid")
    val dateValid: DateValid,
    @SerialName("DateModify")
    val dateModify: DateModify,
    @SerialName("Creator")
    val creator: Creator,
    @SerialName("Category")
    val category: Category?,
) {
    @Serializable
    data class DateValid(
        @SerialName("Date")
        @Serializable(with = CustomDateAdapter::class)
        val date: LocalDate,
        @SerialName("DateDisplay")
        val dateDisplay: String,
        @SerialName("Time")
        val time: String,
        @SerialName("Timestamp")
        val timestamp: Long,
    )

    @Serializable
    data class DateModify(
        @SerialName("Date")
        @Serializable(with = CustomDateAdapter::class)
        val date: LocalDate,
        @SerialName("DateDisplay")
        val dateDisplay: String,
        @SerialName("Time")
        val time: String,
        @SerialName("Timestamp")
        val timestamp: Long,
    )

    @Serializable
    data class Creator(
        @SerialName("Id")
        val id: Int,
        @SerialName("Surname")
        val surname: String,
        @SerialName("Name")
        val name: String,
        @SerialName("DisplayName")
        val displayName: String,
    )

    @Serializable
    data class Category(
        @SerialName("Id")
        val id: Int,
        @SerialName("Name")
        val name: String,
        @SerialName("Type")
        val type: String?,
        @SerialName("DefaultPoints")
        val defaultPoints: Int?,
    )
}
