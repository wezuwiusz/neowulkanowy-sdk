package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames
import java.time.LocalDateTime

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Attendance(

    @SerialName("numerLekcji")
    val number: Int = 0,

    @SerialName("IdPoraLekcji")
    @JsonNames("idPoraLekcji")
    val timeId: Int = 0,

    @SerialName("Data")
    @JsonNames("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("PrzedmiotNazwa")
    @JsonNames("opisZajec")
    val subject: String?,

    @SerialName("IdKategoria")
    @JsonNames("kategoriaFrekwencji")
    val categoryId: Int = -1,
) {

    @Transient
    lateinit var category: AttendanceCategory

    @Transient
    var excusable: Boolean = false

    @Transient
    var excuseStatus: SentExcuseStatus? = null
}
