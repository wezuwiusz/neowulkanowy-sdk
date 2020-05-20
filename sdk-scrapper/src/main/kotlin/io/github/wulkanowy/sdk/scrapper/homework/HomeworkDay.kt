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

        @SerializedName("Attachments")
        val attachments: List<Attachment>
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
