package io.github.wulkanowy.sdk.interfaces

import io.github.wulkanowy.sdk.base.BaseRequest
import io.github.wulkanowy.sdk.dictionaries.DictionariesRequest
import io.github.wulkanowy.sdk.dictionaries.DictionariesResponse
import io.github.wulkanowy.sdk.register.LogResponse
import io.github.wulkanowy.sdk.timetable.TimetableRequest
import io.github.wulkanowy.sdk.timetable.TimetableResponse
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

interface MobileApi {

    @POST("LogAppStart")
    fun logAppStart(@Body logAppStartRequest: BaseRequest): Observable<LogResponse>

    @POST("Slowniki")
    fun getDictionaries(@Body dictionariesRequest: DictionariesRequest): Observable<DictionariesResponse>

    @POST("PlanLekcjiZeZmianami")
    fun getTimetable(@Body timetableRequest: TimetableRequest): Observable<TimetableResponse>
}
