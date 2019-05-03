package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.base.ApiRequest
import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import io.github.wulkanowy.sdk.register.CertificateRequest
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.Student
import io.github.wulkanowy.sdk.service.RegisterService
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RegisterRepository(private val apiKey: String) {

    var baseHost: String = "http://komponenty.vulcan.net.pl"

    var symbol: String = ""

    var certKey: String = ""

    var certificate: String = ""

    fun getRouteByToken(token: String): Single<String> {
        val tokenSymbol = token.substring(0..2)
        if ("FK1" == tokenSymbol) return Single.just("https://api.fakelog.cf")

        return getRegisterApi().getRoutingRules().map { routes ->
            routes.split("\n")
                    .singleOrNull { tokenSymbol == it.substringBefore(",") }?.substringAfter(",")
        }
    }

    fun getCertificate(token: String, pin: String, deviceName: String): Single<CertificateResponse> {
        return getRegisterApi().getCertificate(CertificateRequest(tokenKey = token, pin = pin, deviceName = deviceName))
    }

    fun getPupils(): Single<List<Student>> = getRegisterApi().getPupils(object : ApiRequest() {}).map { it.data }

    private fun getRegisterApi(): RegisterService {
        return Retrofit.Builder()
                .baseUrl("$baseHost/$symbol/mobile-api/Uczen.v3.UczenStart/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(SignInterceptor(apiKey, certificate, certKey))
                        .build()
                )
                .build()
                .create(RegisterService::class.java)
    }
}
