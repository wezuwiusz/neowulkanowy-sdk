package io.github.wulkanowy.sdk.scrapper.mobile

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Format
import pl.droidsonroids.jspoon.annotation.Selector
import java.util.Date

class Device {

    @SerializedName("Id")
    @Selector("td.cellWithButton a", attr = "href", regex = "([^\\/]+\$)")
    var id: Int = 0

    @SerializedName("IdentyfikatorUrzadzenia")
    var deviceId: String? = null

    @SerializedName("NazwaUrzadzenia")
    @Selector("td", index = 0)
    var name: String? = null

    @SerializedName("DataUtworzenia")
    @Format(value = "dd.MM.yyyy 'godz:' HH:mm:ss")
    @Selector("td", index = 1, defValue = "01.01.1970")
    var createDate: Date? = null

    @SerializedName("DataModyfikacji")
    var modificationDate: Date? = null
}
