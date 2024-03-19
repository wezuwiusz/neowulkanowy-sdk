package io.github.wulkanowy.sdk.scrapper.homework

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class HomeworkDetailsPlus(
    
    @SerialName("nauczycielImieNazwisko")
    val teahcer: String,
    
    @SerialName("opis")
    val description: String,
    
    @SerialName("status")
    val status: Int,
    
    @SerialName("odpowiedzWymagana")
    val isAnswerRequired: Boolean,
    
    // TODO: Attachments
)
