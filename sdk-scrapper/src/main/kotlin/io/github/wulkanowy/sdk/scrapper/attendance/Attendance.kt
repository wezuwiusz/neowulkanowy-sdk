package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDateTime

@Serializable
data class Attendance(

    @SerialName("IdPoraLekcji")
    val timeId: Int = 0,

    @SerialName("Data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("PrzedmiotNazwa")
    val subject: String?,

    @SerialName("IdKategoria")
    val categoryId: Int = -1
) {

    @Transient
    var number: Int = 0

    @Transient
    lateinit var category: AttendanceCategory

    @Transient
    var excusable: Boolean = false

    @Transient
    var excuseStatus: SentExcuse.Status? = null
}
