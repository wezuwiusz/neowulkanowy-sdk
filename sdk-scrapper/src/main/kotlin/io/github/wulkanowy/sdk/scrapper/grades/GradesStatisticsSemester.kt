package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class GradesStatisticsSemester(

    @SerialName("Subject")
    val subject: String,

    @SerialName("IsEmpty")
    val isEmpty: Boolean,

    @SerialName("Items")
    val items: List<GradesStatisticsSemesterSubItem>?
)

@Serializable
data class GradesStatisticsSemesterSubItem(

    @SerialName("Label")
    internal val label: String,

    @SerialName("Description")
    internal val description: String,

    @SerialName("Value")
    val amount: Int
) {

    @Transient
    var grade: Int = 0

    @Transient
    var isStudentHere: Boolean = false
}
