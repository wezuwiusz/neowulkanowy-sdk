package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradesStatisticsSemester(

    @Json(name = "Subject")
    val subject: String,

    @Json(name = "IsEmpty")
    val isEmpty: Boolean,

    @Json(name = "Items")
    val items: List<GradesStatisticsSemesterSubItem>?
)

@JsonClass(generateAdapter = true)
data class GradesStatisticsSemesterSubItem(

    @Json(name = "Label")
    internal val label: String,

    @Json(name = "Description")
    internal val description: String,

    @Json(name = "Value")
    val amount: Int
) {

    @Transient
    var grade: Int = 0

    @Transient
    var isStudentHere: Boolean = false
}
