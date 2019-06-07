package io.github.wulkanowy.api.grades

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class DateDeserializer<T : Date>(private val mSimpleDateFormat: SimpleDateFormat, private val mClazz: Class<T>) : JsonDeserializer<T> {

    override fun deserialize(element: JsonElement, arg1: Type, context: JsonDeserializationContext): T {
        val dateString = element.asString
        try {
            return mClazz.newInstance().apply {
                time = mSimpleDateFormat.parse(dateString).time
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
