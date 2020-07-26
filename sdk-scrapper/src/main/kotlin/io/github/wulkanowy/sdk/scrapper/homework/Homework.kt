package io.github.wulkanowy.sdk.scrapper.homework

import com.google.gson.annotations.SerializedName
import java.util.Date

class Homework {

    lateinit var date: Date

    @SerializedName("DataModyfikacji")
    lateinit var entryDate: Date

    @SerializedName("Przedmiot")
    lateinit var subject: String

    @SerializedName("Opis")
    lateinit var content: String

    @SerializedName("Pracownik")
    lateinit var teacher: String

    lateinit var teacherSymbol: String

    @SerializedName("Attachments")
    var attachments: List<String> = emptyList()

    var _attachments: List<Pair<String, String>> = emptyList()
}
