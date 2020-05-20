package io.github.wulkanowy.sdk.scrapper.homework

import com.google.gson.annotations.SerializedName
import java.util.Date

data class HomeworkDay(

    @SerializedName("Date")
    val date: Date,

    @SerializedName("Homework")
    val items: List<Homework>,

    @SerializedName("Show")
    val show: Boolean
) {
    data class Homework(
        @SerializedName("HomeworkId")
        val homeworkId: Int,

        @SerializedName("Subject")
        val subject: String,

        @SerializedName("Teacher")
        val teacher: String,

        @SerializedName("Description")
        val description: String,

        @SerializedName("Date")
        val date: Date,

        @SerializedName("ModificationDate")
        val dateModification: Date,

        @SerializedName("Status")
        val status: String,

        @SerializedName("AnswerRequired")
        val isAnswerRequired: Boolean,

        //
        // @SerializedName("TimeLimit")
        // val timeLimit: Date?,
        //
        @SerializedName("Attachments")
        val attachments: List<Attachment>//,
        //
        // @SerializedName("AnswerDate")
        // val answerDate: Date?,
        //
        // @SerializedName("TeachersComment")
        // val teacherComment: String?,
        //
        // @SerializedName("Answer")
        // val answer: String?,
        //
        // @SerializedName("AnswerAttachments")
        // val answerAttachments: List<String>,
        //
        // @SerializedName("CanReply")
        // val canReplay: Boolean,
        //
        // @SerializedName("Readonly")
        // val readOnly: Boolean,
        //
        // @SerializedName("Id")
        // val id: Long
    ) {
        data class Attachment(

            @SerializedName("IdZadanieDomowe")
            val homeworkId: Int,

            @SerializedName("HtmlTag")
            val html: String,

            @SerializedName("Url")
            val url: String,

            @SerializedName("NazwaPliku")
            val filename: String,

            @SerializedName("IdOneDrive")
            val oneDriveId: String,

            @SerializedName("Id")
            val id: Int
        )
    }
}
