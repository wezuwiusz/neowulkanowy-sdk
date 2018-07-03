package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.base.BaseRequest
import io.github.wulkanowy.sdk.dictionaries.DictionariesRequest
import io.github.wulkanowy.sdk.dictionaries.DictionariesResponse
import io.github.wulkanowy.sdk.grades.GradesRequest
import io.github.wulkanowy.sdk.grades.GradesResponse
import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.interfaces.MobileApi
import io.github.wulkanowy.sdk.register.LogResponse
import io.github.wulkanowy.sdk.timetable.TimetableRequest
import io.github.wulkanowy.sdk.timetable.TimetableResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

class MobileRepository(private val host: String, private val symbol: String, private val signature: String,
                       private val certificate: String, private val reportingUnitSymbol: String) {

    private val api by lazy { getMobileApi() }

    fun logStart(): Observable<LogResponse> = api.logAppStart(object: BaseRequest() {})

    fun getDictionaries(userId: Int, classificationPeriodId: Int, classId: Int): Observable<DictionariesResponse>
            = api.getDictionaries(DictionariesRequest(userId, classificationPeriodId, classId))

    fun getTimetable(startDate: String, endDate: String, classId: Int, classificationPeriodId: Int, studentId: Int): Observable<TimetableResponse> {
        return getMobileApi().getTimetable(TimetableRequest(startDate, endDate, classId, classificationPeriodId, studentId))
    }

    fun getGrades(classId: Int, classificationPeriodId: Int, studentId: Int): Observable<GradesResponse> {
        return getMobileApi().getGrades(GradesRequest(classId, classificationPeriodId, studentId))
    }

    private fun getMobileApi(): MobileApi {
        return Retrofit.Builder()
                .baseUrl("$host/$symbol/$reportingUnitSymbol/mobile-api/Uczen.v3.Uczen/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(SignInterceptor(signature, certificate))
                        .build()
                )
                .build()
                .create(MobileApi::class.java)
    }
}
