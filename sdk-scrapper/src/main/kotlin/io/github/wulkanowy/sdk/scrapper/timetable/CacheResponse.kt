package io.github.wulkanowy.sdk.scrapper.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
class CacheResponse {

    @Json(name = "isParentUser")
    var isParent: Boolean = false

    @Json(name = "poryLekcji")
    lateinit var times: List<Time>

    @Json(name = "isMenuOn")
    var isMenu: Boolean = false

    @Json(name = "pokazLekcjeZrealizowane")
    var showCompletedLessons: Boolean = false

    @JsonClass(generateAdapter = true)
    class Time {

        @Json(name = "Id")
        var id: Int = 0

        @Json(name = "Numer")
        var number: Int = 0

        @Json(name = "Poczatek")
        lateinit var start: Date

        @Json(name = "Koniec")
        lateinit var end: Date

        @Json(name = "DataModyfikacji")
        lateinit var modified: Date

        @Json(name = "IdJednostkaSprawozdawcza")
        var organizationUnitId: Int = 0

        @Json(name = "Nazwa")
        lateinit var name: String
    }
}
