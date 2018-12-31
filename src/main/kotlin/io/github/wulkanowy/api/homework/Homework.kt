package io.github.wulkanowy.api.homework

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.*

class Homework {

    lateinit var date: Date

    @SerializedName("DataModyfikacji")
    @Format("dd.MM.yyyy")
    @Selector(".wartosc", index = 2, regex = ".+, (.+)", defValue = "01.01.1970")
    lateinit var entryDate: Date

    @SerializedName("Przedmiot")
    @Selector(".wartosc", index = 0)
    lateinit var subject: String

    @SerializedName("Opis")
    @Selector(".wartosc", index = 1)
    lateinit var content: String

    @SerializedName("Pracownik")
    @Selector(".wartosc", index = 2, regex = "(.+)\\s\\[.+")
    lateinit var teacher: String

    @Selector(".wartosc", index = 2, regex = "\\[(.+)\\]")
    lateinit var teacherSymbol: String
}
