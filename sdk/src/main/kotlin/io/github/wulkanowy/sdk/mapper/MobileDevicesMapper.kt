package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.scrapper.mobile.Device as ScrapperDevice
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.pojo.Device
import io.github.wulkanowy.sdk.pojo.Token
import io.github.wulkanowy.sdk.toLocalDateTime

fun TokenResponse.mapToken(): Token {
    return Token(
        token = token,
        symbol = symbol,
        pin = pin,
        qrCodeImage = qrCodeImage
    )
}

fun List<ScrapperDevice>.mapDevices(): List<Device> {
    return map {
        Device(
            id = it.id,
            name = it.name,
            date = it.date.toLocalDateTime()
        )
    }
}
