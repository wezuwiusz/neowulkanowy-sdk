package io.github.wulkanowy.sdk.scrapper.homework

@Serializable
data class ExamHomeworkPlus(
    
    @SerialName("typ")
    val type: Int,
    
    @SerialName("przedmiotNazwa")
    val subject: String,
    
    @SerialName("data")
    val date: LocalDateTime,
    
    @SerialName("id")
    val id: Int
)
