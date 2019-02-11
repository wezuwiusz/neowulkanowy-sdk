package io.github.wulkanowy.api.realized

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.Date

class Realized {

    @SerializedName("Data")
    @Selector("h2", defValue = "01.01.1970")
    @Format("dd.MM.yyyy")
    lateinit var date: Date

    @SerializedName("NrLekcji")
    @Selector(".wartosc", index = 0, defValue = "0")
    var number: Int = 0

    @SerializedName("Przedmiot")
    @Selector(".wartosc", index = 1, regex = "^(.+?),", defValue = "")
    var subject: String = ""

    @SerializedName("Temat")
    @Selector(".wartosc", index = 1, regex = "[^,]*, (.*)", defValue = "")
    var topic: String = ""

    @SerializedName("Nauczyciel")
    @Selector(".wartosc", index = 2, regex = "(.+)\\s\\[.+")
    var teacher: String = ""

    @Selector(".wartosc", index = 2, regex = "\\[(.+)\\]")
    var teacherSymbol: String = ""

    @SerializedName("Nieobecnosc")
    @Selector(".wartosc", index = 3)
    var absence: String = ""
}
