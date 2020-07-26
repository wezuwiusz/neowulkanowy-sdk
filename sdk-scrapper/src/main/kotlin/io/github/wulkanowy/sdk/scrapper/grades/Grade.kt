package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName
import java.util.Date

class Grade {

    var subject: String = ""

    @SerializedName("Wpis")
    var entry: String = ""

    var value: Int = 0

    var modifier: Double = .0

    var comment: String = ""

    @SerializedName("KolorOceny") // dec
    var color: String = ""

    @SerializedName("KodKolumny")
    var symbol: String? = ""

    @SerializedName("NazwaKolumny")
    var description: String? = ""

    var weight: String = ""

    @SerializedName("Waga")
    var weightValue: Double = .0

    @SerializedName("DataOceny")
    internal var privateDate: GradeDate = GradeDate()

    var date: Date = Date()

    @SerializedName("Nauczyciel")
    var teacher: String = ""
}

class GradeDate : Date() {
    companion object {
        const val FORMAT = "dd.MM.yyyy"
    }
}
