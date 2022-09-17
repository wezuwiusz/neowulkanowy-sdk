package io.github.wulkanowy.sdk.scrapper.adapter

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
object CustomDateAdapter : KSerializer<LocalDateTime> {

    private const val DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    private const val DATE_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSXXX"
    private const val DATE_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ss.SXXX"
    private const val DATE_FORMAT_4 = "yyyy-MM-dd'T'HH:mm:ssXXX"
    private const val DATE_FORMAT_5 = "yyyy-MM-dd HH:mm:ss"

    private val formatter = DateTimeFormatter.ofPattern("[$DATE_FORMAT_1][$DATE_FORMAT_2][$DATE_FORMAT_3][$DATE_FORMAT_4][$DATE_FORMAT_5]")

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val date = decoder.decodeString()

        return LocalDateTime.parse(date, formatter)
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }
}
