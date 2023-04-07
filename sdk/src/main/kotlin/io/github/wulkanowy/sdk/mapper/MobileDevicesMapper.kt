package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Device
import io.github.wulkanowy.sdk.pojo.Token
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import java.time.LocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.scrapper.mobile.Device as ScrapperDevice

fun TokenResponse.mapToken() = Token(
    token = token,
    symbol = symbol,
    pin = pin,
    qrCodeImage = qrCodeImage,
)

fun List<ScrapperDevice>.mapDevices(zoneId: ZoneId) = map {
    Device(
        id = it.id,
        deviceId = it.deviceId.orEmpty(),
        name = it.name.orEmpty(),
        createDate = (it.createDate ?: LocalDateTime.now()).atZone(zoneId),
        modificationDate = it.modificationDate?.atZone(zoneId),
    )
}
