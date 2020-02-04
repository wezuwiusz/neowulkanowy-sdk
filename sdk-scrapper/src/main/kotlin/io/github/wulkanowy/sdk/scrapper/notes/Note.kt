package io.github.wulkanowy.sdk.scrapper.notes

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.Date

class Note {

    @SerializedName("DataWpisu")
    lateinit var date: Date

    @SerializedName("Nauczyciel")
    @Selector(".wartosc", index = 0, regex = "(.+)\\s\\[.+")
    lateinit var teacher: String

    @Selector(".wartosc", index = 0, regex = "\\[(.+)\\]")
    lateinit var teacherSymbol: String

    @SerializedName("Kategoria")
    @Selector(".wartosc", index = 1)
    lateinit var category: String

    @SerializedName("TrescUwagi")
    @Selector(".wartosc", index = 2)
    lateinit var content: String

    @SerializedName("Punkty")
    lateinit var points: String

    @SerializedName("PokazPunkty")
    var showPoints = false

    @SerializedName("KategoriaTyp")
    var categoryType: Int = 0
}
