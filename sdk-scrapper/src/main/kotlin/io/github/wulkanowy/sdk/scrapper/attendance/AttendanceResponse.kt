package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName

data class AttendanceResponse(

    @SerializedName("UsprawiedliwieniaAktywne")
    val excuseActive: Boolean,

    @SerializedName("Frekwencje")
    val lessons: List<Attendance>,

    @SerializedName("UsprawiedliwieniaWyslane")
    val sentExcuses: List<SentExcuse>
)
