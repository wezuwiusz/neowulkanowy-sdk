package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Attendance(

    @Json(name = "IdPoraLekcji")
    val timeId: Int = 0,

    @Json(name = "Data")
    val date: Date,

    @Json(name = "PrzedmiotNazwa")
    val subject: String?,

    @Json(name = "IdKategoria")
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
