package io.github.wulkanowy.api.grades

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import io.github.wulkanowy.api.toDate
import java.lang.reflect.Type
import java.util.Date

class DateDeserializer<T : Date>(private val mClazz: Class<T>) : JsonDeserializer<T> {

    override fun deserialize(element: JsonElement, arg1: Type, context: JsonDeserializationContext): T {
        val dateString = element.asString
        return try {
            mClazz.newInstance().apply {
                time = dateString.toDate(GradeDate.FORMAT).time
            }
        } catch (e: Throwable) {
            throw JsonParseException("Error while parse date format: $dateString", e)
        }
    }
}
