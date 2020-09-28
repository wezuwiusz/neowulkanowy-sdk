package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
class SentExcuse {

    @Json(name = "Status")
    var status: Int = 0

    @Json(name = "Dzien")
    lateinit var date: Date

    @Json(name = "IdPoraLekcji")
    var timeId: Int? = null

    enum class Status(val id: Int) {
        WAITING(0),
        ACCEPTED(1),
        DENIED(2);

        companion object {
            fun getByValue(value: Int) = values().firstOrNull { it.id == value }
        }
    }
}
