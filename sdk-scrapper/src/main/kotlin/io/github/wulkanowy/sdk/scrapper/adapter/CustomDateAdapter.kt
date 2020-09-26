package io.github.wulkanowy.sdk.scrapper.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomDateAdapter : JsonAdapter<Date>() {

    private val dateFormat = SimpleDateFormat(SERVER_FORMAT, Locale.getDefault())

    companion object {
        const val SERVER_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }

    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        val dateAsString = reader.nextString()
        return synchronized(dateFormat) {
            dateFormat.parse(dateAsString)
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            synchronized(dateFormat) {
                writer.value(dateFormat.format(value))
            }
        }
    }
}
