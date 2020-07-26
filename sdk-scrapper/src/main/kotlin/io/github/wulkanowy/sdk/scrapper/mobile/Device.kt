package io.github.wulkanowy.sdk.scrapper.mobile

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Device(

    @SerializedName("Id")
    val id: Int = 0,

    @SerializedName("IdentyfikatorUrzadzenia")
    val deviceId: String? = null,

    @SerializedName("NazwaUrzadzenia")
    val name: String? = null,

    @SerializedName("DataUtworzenia")
    val createDate: Date? = null,

    @SerializedName("DataModyfikacji")
    val modificationDate: Date? = null
)
