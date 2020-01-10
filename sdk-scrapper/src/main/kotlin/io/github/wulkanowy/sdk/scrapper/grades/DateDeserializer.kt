package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import io.github.wulkanowy.sdk.scrapper.toDate
import java.lang.reflect.Type
import java.util.Date

class DateDeserializer<T : Date>(private val mClazz: Class<T>) : JsonDeserializer<T> {

    override fun deserialize(element: JsonElement, arg1: Type, context: JsonDeserializationContext): T {
        val dateString = element.asString
        return try {
            mClazz.getDeclaredConstructor().newInstance().apply {
                time = dateString.toDate(GradeDate.FORMAT).time
            }
        } catch (e: Throwable) {
            throw JsonParseException("Error while parse date format: $dateString", e)
        }
    }
}
