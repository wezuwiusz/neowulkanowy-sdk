package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Grade(

    @Json(name = "Wpis")
    val entry: String = "",

    @Json(name = "KolorOceny") // dec
    val color: String = "",

    @Json(name = "KodKolumny")
    val symbol: String? = "",

    @Json(name = "NazwaKolumny")
    val description: String? = "",

    @Json(name = "Waga")
    val weightValue: Double = .0,

    @Json(name = "DataOceny")
    internal val privateDate: GradeDate? = GradeDate(),

    @Json(name = "Nauczyciel")
    val teacher: String = ""
) {

    @Transient
    var subject: String = ""

    @Transient
    var value: Int = 0

    @Transient
    var modifier: Double = .0

    @Transient
    var comment: String = ""

    @Transient
    var weight: String = ""

    @Transient
    var date: Date = Date()
}

class GradeDate : Date() {
    companion object {
        const val FORMAT = "dd.MM.yyyy"
    }
}
