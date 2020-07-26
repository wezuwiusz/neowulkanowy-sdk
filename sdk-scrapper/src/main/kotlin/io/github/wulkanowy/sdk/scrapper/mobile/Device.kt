package io.github.wulkanowy.sdk.scrapper.mobile

import com.google.gson.annotations.SerializedName
import java.util.Date

class Device {

    @SerializedName("Id")
    var id: Int = 0

    @SerializedName("IdentyfikatorUrzadzenia")
    var deviceId: String? = null

    @SerializedName("NazwaUrzadzenia")
    var name: String? = null

    @SerializedName("DataUtworzenia")
    var createDate: Date? = null

    @SerializedName("DataModyfikacji")
    var modificationDate: Date? = null
}
