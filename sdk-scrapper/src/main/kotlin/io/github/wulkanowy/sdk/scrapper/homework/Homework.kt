package io.github.wulkanowy.sdk.scrapper.homework

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Homework(
    @SerializedName("HomeworkId")
    val homeworkId: Int,

    @SerializedName("Subject")
    val subject: String,

    @SerializedName("Teacher")
    val teacher: String,

    @SerializedName("Description")
    val content: String,

    @SerializedName("Date")
    val date: Date,

    @SerializedName("ModificationDate")
    val entryDate: Date,

    @SerializedName("Status")
    val status: String,

    @SerializedName("AnswerRequired")
    val isAnswerRequired: Boolean,

    @SerializedName("Attachments")
    val attachments: List<HomeworkAttachment>
) {

    lateinit var teacherSymbol: String

    var _attachments: List<Pair<String, String>> = emptyList()
}
