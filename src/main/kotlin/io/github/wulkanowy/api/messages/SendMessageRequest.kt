package io.github.wulkanowy.api.messages

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(

        @SerializedName("incomming")
        val incoming: Incoming

)
