package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.base.BaseRequest
import io.github.wulkanowy.sdk.dictionaries.DictionariesRequest
import io.github.wulkanowy.sdk.dictionaries.DictionariesResponse
import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.interfaces.MobileApi
import io.github.wulkanowy.sdk.register.LogResponse
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
