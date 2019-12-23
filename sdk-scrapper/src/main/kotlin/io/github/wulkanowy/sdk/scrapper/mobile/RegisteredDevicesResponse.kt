package io.github.wulkanowy.sdk.scrapper.mobile

import pl.droidsonroids.jspoon.annotation.Selector

class RegisteredDevicesResponse {

    @Selector(".mainContainer > table tbody tr")
    var devices: List<Device> = emptyList()
}
