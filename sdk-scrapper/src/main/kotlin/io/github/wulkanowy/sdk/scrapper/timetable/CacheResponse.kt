package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal class CacheResponse {

    @SerialName("isParentUser")
    var isParent: Boolean = false

    @SerialName("poryLekcji")
    var times: List<Time> = emptyList()

    @SerialName("isMenuOn")
    var isMenu: Boolean = false

    @SerialName("pokazLekcjeZrealizowane")
    var showCompletedLessons: Boolean = false

    @Serializable
    class Time {

        @SerialName("Id")
        var id: Int = 0

        @SerialName("Numer")
        var number: Int = 0

        @SerialName("Poczatek")
        @Serializable(with = CustomDateAdapter::class)
        lateinit var start: LocalDateTime

        @SerialName("Koniec")
        @Serializable(with = CustomDateAdapter::class)
        lateinit var end: LocalDateTime

        @SerialName("DataModyfikacji")
        @Serializable(with = CustomDateAdapter::class)
        lateinit var modified: LocalDateTime

        @SerialName("IdJednostkaSprawozdawcza")
        var organizationUnitId: Int = 0

        @SerialName("Nazwa")
        lateinit var name: String
    }
}
