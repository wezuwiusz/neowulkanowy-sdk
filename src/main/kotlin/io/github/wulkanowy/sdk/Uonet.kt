package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.RegisterApi
import io.github.wulkanowy.sdk.register.StudentsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

class Uonet(private val host: String, private val symbol: String, var signature: String = "", var certificate: String = "") {

    fun getCertificate(token: String, pin: String, deviceName: String): Observable<CertificateResponse> {
        return getRegisterApi().getCertificate(CertificateRequest(tokenKey = token, pin = pin, deviceName = deviceName))
    }

    fun getPupils(): Observable<StudentsResponse> {
        return getRegisterApi().getPupils(object: BaseRequest() {})
    }

    private fun getRegisterApi() : RegisterApi {
        return getRetrofit("Uczen.v3.UczenStart/", signature, certificate).create(RegisterApi::class.java)
    }

    private fun getRetrofit(baseUrl: String, signature: String = "", certificate: String = ""): Retrofit {
        return Retrofit.Builder()
                .baseUrl("$host/$symbol/mobile-api/$baseUrl")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(SignInterceptor(signature, certificate))
                        .build()
                )
                .build()
    }
}
