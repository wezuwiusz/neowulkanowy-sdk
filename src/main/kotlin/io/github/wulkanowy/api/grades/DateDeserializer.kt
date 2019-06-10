package io.github.wulkanowy.api.grades

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import io.github.wulkanowy.api.toDate
import java.lang.reflect.Type
import java.text.ParseException
import java.util.Date

class DateDeserializer<T : Date>(private val mClazz: Class<T>) : JsonDeserializer<T> {

    override fun deserialize(element: JsonElement, arg1: Type, context: JsonDeserializationContext): T {
        val dateString = element.asString
        try {
            return mClazz.newInstance().apply {
                time = dateString.toDate(GradeDate.FORMAT).time
            }
        } catch (e: InstantiationException) {
            throw JsonParseException(e.message, e)
        } catch (e: IllegalAccessException) {
            throw JsonParseException(e.message, e)
        } catch (e: ParseException) {
            throw JsonParseException(e.message, e)
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw IllegalArgumentException("Invalid date format: $dateString", e)
        }
    }
}
