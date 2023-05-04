package io.github.wulkanowy.sdk.hebe

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
internal object CustomDateAdapter : KSerializer<LocalDate> {

    private const val DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    private const val DATE_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSXXX"
    private const val DATE_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ss.SXXX"
    private const val DATE_FORMAT_4 = "yyyy-MM-dd'T'HH:mm:ssXXX"
    private const val DATE_FORMAT_5 = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT_6 = "yyyy-MM-dd"

    private val formatter = DateTimeFormatter.ofPattern("[$DATE_FORMAT_1][$DATE_FORMAT_2][$DATE_FORMAT_3][$DATE_FORMAT_4][$DATE_FORMAT_5][$DATE_FORMAT_6]")

    override fun deserialize(decoder: Decoder): LocalDate {
        val date = decoder.decodeString()

        return LocalDate.parse(date, formatter)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }
}
