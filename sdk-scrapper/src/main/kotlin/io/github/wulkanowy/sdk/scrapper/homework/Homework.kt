package io.github.wulkanowy.sdk.scrapper.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Homework(

    @Json(name = "HomeworkId")
    val homeworkId: Int,

    @Json(name = "Subject")
    val subject: String,

    @Json(name = "Teacher")
    val teacher: String,

    @Json(name = "Description")
    val content: String,

    @Json(name = "Date")
    val date: Date,

    @Json(name = "ModificationDate")
    val entryDate: Date,

    @Json(name = "Status")
    val status: String,

    @Json(name = "AnswerRequired")
    val isAnswerRequired: Boolean,

    @Json(name = "Attachments")
    val attachments: List<HomeworkAttachment>
) {

    @Transient
    lateinit var teacherSymbol: String

    @Transient
    var _attachments: List<Pair<String, String>> = emptyList()
}
