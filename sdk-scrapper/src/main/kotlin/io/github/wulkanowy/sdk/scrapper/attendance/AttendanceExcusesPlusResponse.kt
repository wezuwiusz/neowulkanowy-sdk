package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class AttendanceExcusesPlusResponse(
    @SerialName("usprawiedliwieniaAktywne")
    val isExcusesActive: Boolean,
    @SerialName("usprawiedliwienia")
    val excuses: List<AttendanceExcusePlusResponseItem>,
    @SerialName("usprawiedliwieniaBlokada")
    val excusesBlocked: UsprawiedliwieniaBlokada,
)

@Serializable
internal data class AttendanceExcusePlusResponseItem(
    @SerialName("dzien")
    @Serializable(with = CustomDateAdapter::class)
    val dayDate: LocalDateTime,
    @SerialName("idUsprawiedliwienieDzien")
    val idExcuseDay: Int,
    @SerialName("idUsprawiedliwienieLekcjaOddzial")
    val idUExcuseLessonClass: Int,
    @SerialName("numerLekcji")
    val lessonNumber: Int?,
    @SerialName("status")
    val status: Int,
)

@Serializable
internal data class UsprawiedliwieniaBlokada(
    @SerialName("dataOd")
    val dateFrom: String?,
    @SerialName("dataDo")
    val dateTo: String?,
    @SerialName("obowiazujeDo")
    val obowiazujeDo: String?,
    @SerialName("oddzialy")
    val classes: Oddzialy,
)

@Serializable
internal class Oddzialy
