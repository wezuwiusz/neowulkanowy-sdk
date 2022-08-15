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

    private const val FIRST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    private const val SECOND_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    private val formatter = DateTimeFormatter.ofPattern("[$SECOND_DATE_FORMAT][$FIRST_DATE_FORMAT]")

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val date = decoder.decodeString()

        return LocalDateTime.parse(date, formatter)
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }
}
