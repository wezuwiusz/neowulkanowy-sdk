package io.github.wulkanowy.sdk.scrapper.timetable

import com.google.gson.annotations.SerializedName
import java.util.Date

class CacheResponse {

    @SerializedName("czyOpiekun")
    var isParent: Boolean = false

    @SerializedName("poryLekcji")
    lateinit var times: List<Time>

    class Time {

        @SerializedName("Id")
        var id: Int = 0

        @SerializedName("Numer")
        var number: Int = 0

        @SerializedName("Poczatek")
        lateinit var start: Date

        @SerializedName("Koniec")
        lateinit var end: Date

        @SerializedName("DataModyfikacji")
        lateinit var modified: Date

        @SerializedName("IdJednostkaSprawozdawcza")
        var organizationUnitId: Int = 0

        @SerializedName("Nazwa")
        lateinit var name: String
    }
}
