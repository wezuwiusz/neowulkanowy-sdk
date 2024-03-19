package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDateTime

@Serializable
data class Homework(

    @SerialName("HomeworkId")
    val homeworkId: Int,

    @SerialName("Subject")
    val subject: String,

    @SerialName("Teacher")
    val teacher: String,

    @SerialName("Description")
    val content: String,

    @SerialName("Date")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("ModificationDate")
    @Serializable(with = CustomDateAdapter::class)
    val entryDate: LocalDateTime,

    @SerialName("Status")
    val status: Int,

    @SerialName("AnswerRequired")
    val isAnswerRequired: Boolean,

    @SerialName("Attachments")
    val attachments: List<HomeworkAttachment> = emptyList(),
) {

    @Transient
    lateinit var teacherSymbol: String

    @Transient
    @Suppress("PropertyName")
    var _attachments: List<Pair<String, String>> = emptyList()
}
