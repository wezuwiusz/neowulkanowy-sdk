package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class AttendanceSummaryItemSerializer : JsonSerializer<AttendanceSummaryResponse.Summary> {

    override fun serialize(src: AttendanceSummaryResponse.Summary, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonObject().apply {
            add("Id", context.serialize(src.id))
            add("NazwaTypuFrekwencji", context.serialize(src.type))
            add("Wrzesien", context.serialize(src.september))
            add("Pazdziernik", context.serialize(src.october))
            add("Listopad", context.serialize(src.november))
            add("Grudzien", context.serialize(src.december))
            add("Styczen", context.serialize(src.january))
            add("Luty", context.serialize(src.february))
            add("Marzec", context.serialize(src.march))
            add("Kwiecien", context.serialize(src.april))
            add("Maj", context.serialize(src.may))
            add("Czerwiec", context.serialize(src.june))
            add("Lipiec", context.serialize(src.july))
            add("Sierpien", context.serialize(src.august))
            add("Razem", context.serialize(src.total))
        }
    }
}
