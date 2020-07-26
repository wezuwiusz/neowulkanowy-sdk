package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Grade(

    @SerializedName("Wpis")
    val entry: String = "",

    @SerializedName("KolorOceny") // dec
    val color: String = "",

    @SerializedName("KodKolumny")
    val symbol: String? = "",

    @SerializedName("NazwaKolumny")
    val description: String? = "",

    @SerializedName("Waga")
    val weightValue: Double = .0,

    @SerializedName("DataOceny")
    internal val privateDate: GradeDate = GradeDate(),

    @SerializedName("Nauczyciel")
    val teacher: String = ""
) {

    var subject: String = ""

    var value: Int = 0

    var modifier: Double = .0

    var comment: String = ""

    var weight: String = ""

    var date: Date = Date()
}

class GradeDate : Date() {
    companion object {
        const val FORMAT = "dd.MM.yyyy"
    }
}
