package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradeStatistics(

    @Json(name = "Value")
    val amount: Int? = 0
) {

    @Transient
    var semesterId: Int = 0

    @Transient
    lateinit var subject: String

    @Transient
    lateinit var grade: String

    @Transient
    var gradeValue: Int = 0
}
