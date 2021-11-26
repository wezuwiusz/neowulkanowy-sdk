package io.github.wulkanowy.sdk.scrapper.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import io.github.wulkanowy.sdk.scrapper.grades.GradeDate
import io.github.wulkanowy.sdk.scrapper.toDate

class GradeDateDeserializer : JsonAdapter<GradeDate?>() {

    companion object {
        const val SERVER_FORMAT = GradeDate.FORMAT
    }

    @FromJson
    override fun fromJson(reader: JsonReader): GradeDate? {
        val value = reader.readJsonValue()
        val dateAsString = value?.toString()
        return synchronized(reader) {
            GradeDate::class.java.getDeclaredConstructor().newInstance().apply {
                time = (dateAsString ?: "01.01.1970").toDate(SERVER_FORMAT).time
            }
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: GradeDate?) {
        throw NotImplementedError()
    }
}
