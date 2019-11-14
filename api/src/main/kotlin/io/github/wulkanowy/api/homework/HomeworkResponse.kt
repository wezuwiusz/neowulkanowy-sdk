package io.github.wulkanowy.api.homework

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.Date

class HomeworkResponse {

    @SerializedName("Data")
    @Format("dd.MM.yyyy")
    @Selector(".mainContainer h2:not(:contains(Brak))", regex = "\\s(.*)", defValue = "01.01.1970")
    lateinit var date: Date

    @SerializedName("ZadaniaDomowe")
    @Selector(".mainContainer article")
    var items: List<Homework> = emptyList()
}
