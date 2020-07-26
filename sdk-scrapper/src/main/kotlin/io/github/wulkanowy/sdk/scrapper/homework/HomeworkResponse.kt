package io.github.wulkanowy.sdk.scrapper.homework

import com.google.gson.annotations.SerializedName
import java.util.Date

class HomeworkResponse {

    @SerializedName("Data")
    lateinit var date: Date

    @SerializedName("ZadaniaDomowe")
    var items: List<Homework> = emptyList()
}
