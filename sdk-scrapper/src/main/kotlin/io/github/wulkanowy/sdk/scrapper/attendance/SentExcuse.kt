package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName
import java.util.Date

class SentExcuse {

    @SerializedName("Status")
    var status: Int = 0

    @SerializedName("Dzien")
    lateinit var date: Date

    @SerializedName("IdPoraLekcji")
    var timeId: Int = 0

    enum class Status(val id: Int) {
        WAITING(0),
        ACCEPTED(1),
        DENIED(2);

        companion object {
            fun getByValue(value: Int) = values().firstOrNull { it.id == value }
        }
    }
}
