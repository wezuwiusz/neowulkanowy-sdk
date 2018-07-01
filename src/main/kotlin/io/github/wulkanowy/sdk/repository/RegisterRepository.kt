package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.base.BaseRequest
import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.interfaces.RegisterApi
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.StudentsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

class RegisterRepository(private val host: String, private val symbol: String, var signature: String = "", var certificate: String = "") {

    fun getCertificate(token: String, pin: String, deviceName: String): Observable<CertificateResponse> {
        return getRegisterApi().getCertificate(CertificateRequest(tokenKey = token, pin = pin, deviceName = deviceName))
    }

    fun getPupils(): Observable<StudentsResponse> = getRegisterApi().getPupils(object: BaseRequest() {})

    private fun getRegisterApi(): RegisterApi {
        return Retrofit.Builder()
                .baseUrl("$host/$symbol/mobile-api/Uczen.v3.UczenStart/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(SignInterceptor(signature, certificate))
                        .build()
                )
                .build()
                .create(RegisterApi::class.java)
    }
}
