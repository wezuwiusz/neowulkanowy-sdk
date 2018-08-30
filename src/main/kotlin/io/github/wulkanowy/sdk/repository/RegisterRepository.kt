package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.base.ApiRequest
import io.github.wulkanowy.sdk.base.ApiResponse
import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.github.wulkanowy.sdk.service.RegisterService
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RegisterRepository(private val host: String, private val symbol: String, var signature: String = "", var certificate: String = "") {

    fun getCertificate(token: String, pin: String, deviceName: String): Observable<CertificateResponse> {
        return getRegisterApi().getCertificate(CertificateRequest(tokenKey = token, pin = pin, deviceName = deviceName))
    }

    fun getPupils(): Observable<ApiResponse<List<Student>>> = getRegisterApi().getPupils(object: ApiRequest() {})

    private fun getRegisterApi(): RegisterService {
        return Retrofit.Builder()
                .baseUrl("$host/$symbol/mobile-api/Uczen.v3.UczenStart/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(SignInterceptor(signature, certificate))
                        .build()
                )
                .build()
                .create(RegisterService::class.java)
    }
}
