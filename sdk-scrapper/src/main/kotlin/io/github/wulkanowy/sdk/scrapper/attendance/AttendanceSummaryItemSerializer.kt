package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class AttendanceSummaryItemSerializer : JsonAdapter<AttendanceSummaryResponse.Summary>() {

    override fun fromJson(reader: JsonReader): AttendanceSummaryResponse.Summary? {
        throw IllegalStateException("Not implemented")
    }

    override fun toJson(writer: JsonWriter, src: AttendanceSummaryResponse.Summary?) {
        writer.run {
            beginObject()
            name("Id").value(src?.id)
            name("NazwaTypuFrekwencji").value(src?.type)
            name("Wrzesien").value(src?.september)
            name("Pazdziernik").value(src?.october)
            name("Listopad").value(src?.november)
            name("Grudzien").value(src?.december)
            name("Styczen").value(src?.january)
            name("Luty").value(src?.february)
            name("Marzec").value(src?.march)
            name("Kwiecien").value(src?.april)
            name("Maj").value(src?.may)
            name("Czerwiec").value(src?.june)
            name("Lipiec").value(src?.july)
            name("Sierpien").value(src?.august)
            name("Razem").value(src?.total)
        }
    }
}
