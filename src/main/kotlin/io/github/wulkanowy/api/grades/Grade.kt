package io.github.wulkanowy.api.grades

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.text.SimpleDateFormat
import java.util.*

class Grade {

    @Selector("td", index = 0)
    var subject: String = ""

    @SerializedName("Wpis")
    @Selector("td", index = 1, regex = "([^\\s]*)")
    var entry: String = ""

    @Selector("td", index = 1, converter = GradeValueConverter::class)
    var value: Int = 0

    @Selector("td", index = 1, converter = GradeModifierValueConverter::class)
    var modifier: Double = .0

    @Selector("td", index = 1, regex = "\\(([^)]+)\\)")
    var comment: String = ""

    @SerializedName("KolorOceny") //dec
    @Selector("td .ocenaCzastkowa", attr = "style", regex = "#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})") //hex
    var color: String = ""

    @SerializedName("KodKolumny")
    @Selector("td", index = 2, regex = "^(.+?),")
    var symbol: String? = ""

    @SerializedName("NazwaKolumny")
    @Selector("td", index = 2, regex = "[^,]+, (.+)", defValue = "")
    var description: String? = ""

    @Selector("td", index = 3)
    var weight: String = ""

    @SerializedName("Waga")
    @Selector("td", index = 3, converter = GradeWeightValueConverter::class)
    var weightValue: Int = 0

    @SerializedName("DataOceny")
    var privateDate: GradeDate = GradeDate()

    @Selector("td:not(:empty)", index = 4, defValue = "01.01.1970")
    @Format(GradeDate.FORMAT)
    var date: Date = Date()

    @SerializedName("Nauczyciel")
    @Selector("td", index = 5)
    var teacher: String = ""
}

class GradeDate : Date() {
    companion object {
        const val FORMAT = "dd.MM.yyyy"
        val DATE_FORMAT = SimpleDateFormat(FORMAT)
    }
}
