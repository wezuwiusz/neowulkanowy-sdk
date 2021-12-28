package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Device
import io.github.wulkanowy.sdk.pojo.Token
import io.github.wulkanowy.sdk.scrapper.mobile.TokenResponse
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId
import io.github.wulkanowy.sdk.scrapper.mobile.Device as ScrapperDevice

fun TokenResponse.mapToken() = Token(
    token = token,
    symbol = symbol,
    pin = pin,
    qrCodeImage = qrCodeImage
)

fun List<ScrapperDevice>.mapDevices(zoneId: ZoneId) = map {
    Device(
        id = it.id,
        deviceId = it.deviceId.orEmpty(),
        name = it.name.orEmpty(),
        createDate = it.createDate?.toLocalDateTime() ?: now(),
        modificationDate = it.modificationDate?.toLocalDateTime(),
        createDateZoned = (it.createDate?.toLocalDateTime() ?: now()).atZone(zoneId),
        modificationDateZoned = it.modificationDate?.toLocalDateTime()?.atZone(zoneId)
    )
}
