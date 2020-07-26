package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName

class AttendanceResponse {

    @SerializedName("UsprawiedliwieniaAktywne")
    var excuseActive: Boolean = false

    @SerializedName("Frekwencje")
    var lessons: List<Attendance> = emptyList()

    @SerializedName("UsprawiedliwieniaWyslane")
    var sentExcuses: List<SentExcuse> = emptyList()
}
