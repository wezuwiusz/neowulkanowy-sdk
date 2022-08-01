package io.github.wulkanowy.sdk.scrapper.adapter

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Object::class)
object ObjectSerializer : KSerializer<Any> {

    override fun deserialize(decoder: Decoder): Any = Any()

    override fun serialize(encoder: Encoder, value: Any) {
        encoder.encodeInline(AnyObject.serializer().descriptor)
    }

    @Serializable
    private class AnyObject
}
