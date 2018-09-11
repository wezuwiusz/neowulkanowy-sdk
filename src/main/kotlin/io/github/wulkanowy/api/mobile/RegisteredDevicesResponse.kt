package io.github.wulkanowy.api.mobile

import pl.droidsonroids.jspoon.annotation.Selector

class RegisteredDevicesResponse {

    @Selector(".mainContainer > table tbody tr")
    var devices: List<Device> = emptyList()
}
