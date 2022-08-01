package io.github.wulkanowy.sdk.scrapper.adapter

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
object GradeDateDeserializer : KSerializer<LocalDate> {

    private const val SERVER_FORMAT = "dd.MM.yyyy"
    private const val SERVER_FORMAT_2 = "dd.M.yyyy"
    private val formatter = DateTimeFormatter.ofPattern("[$SERVER_FORMAT][$SERVER_FORMAT_2]")

    override fun deserialize(decoder: Decoder): LocalDate {
        val date = if (decoder.decodeNotNullMark()) {
            decoder.decodeString()
        } else "01.01.1970"

        return LocalDate.parse(date, formatter)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }
}
