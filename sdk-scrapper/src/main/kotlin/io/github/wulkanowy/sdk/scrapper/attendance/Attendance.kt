package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Attendance(

    @SerializedName("IdPoraLekcji")
    val timeId: Int = 0,

    @SerializedName("Data")
    val date: Date,

    @SerializedName("PrzedmiotNazwa")
    val subject: String,

    @SerializedName("IdKategoria")
    val categoryId: Int = -1
) {

    var number: Int = 0

    lateinit var category: AttendanceCategory

    var excusable: Boolean = false

    var excuseStatus: SentExcuse.Status? = null
}
