package io.github.wulkanowy.api.service

import RxJava2ReauthCallAdapterFactory
import com.google.gson.GsonBuilder
import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.StudentAndParentInterceptor
import io.github.wulkanowy.api.login.NotLoggedInException
import io.github.wulkanowy.api.repository.LoginRepository
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class ServiceManager(
        logLevel: HttpLoggingInterceptor.Level,
        private val loginType: Api.LoginType,
        private val schema: String,
        private val host: String,
        private val symbol: String,
        private val email: String,
        private val password: String,
        private val schoolSymbol: String,
        private val studentId: Int,
        private val diaryId: Int
) {

    private val cookies by lazy {
        CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
    }

    private val url by lazy {
        UrlGenerator(schema, host, symbol, schoolSymbol)
    }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf(
            Pair(HttpLoggingInterceptor().setLevel(logLevel), true),
            Pair(ErrorInterceptor(), false)
    )

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false, index: Int = -1) {
        if (index == -1) interceptors.add(Pair(interceptor, network))
        else interceptors.add(index, Pair(interceptor, network))
    }

    fun getCookieManager(): CookieManager {
        return cookies
    }

    fun getLoginService(): LoginService {
        if (email.isBlank() || password.isBlank()) throw ApiException("Email or/and password are not set")
        return getRetrofit(getClientBuilder(), url.generate(UrlGenerator.Site.LOGIN), false).create(LoginService::class.java)
    }

    fun getRegisterService(): RegisterService {
        return getRetrofit(getClientBuilder(false, true), url.generate(UrlGenerator.Site.LOGIN), false).create(RegisterService::class.java)
    }

    fun getSnpService(withLogin: Boolean = true, interceptor: Boolean = true): StudentAndParentService {
        if (withLogin && schoolSymbol.isBlank()) throw ApiException("School id is not set")

        val client = getClientBuilder()
        if (interceptor) {
            if (0 == diaryId || 0 == studentId) throw ApiException("Student or/and diaryId id are not set")
            client.addInterceptor(StudentAndParentInterceptor(cookies, schema, host, diaryId, studentId))
        }

        return getRetrofit(client, url.generate(UrlGenerator.Site.SNP), withLogin).create(StudentAndParentService::class.java)
    }

    fun getMessagesService(): MessagesService {
        return getRetrofit(getClientBuilder(), url.generate(UrlGenerator.Site.MESSAGES), true, true).create(MessagesService::class.java)
    }

    private fun getRetrofit(client: OkHttpClient.Builder, baseUrl: String, login: Boolean = true, gson: Boolean = false): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(if (gson) GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()) else JspoonConverterFactory.create())
                .addCallAdapterFactory(if (!login) RxJava2CallAdapterFactory.create() else
                    RxJava2ReauthCallAdapterFactory.create(
                            LoginRepository(loginType, schema, host, symbol, cookies, getLoginService()).login(email, password).toFlowable(),
                            { it is NotLoggedInException }
                    )
                ).build()
    }

    private fun getClientBuilder(errorInterceptor: Boolean = true, separateJar: Boolean = false): OkHttpClient.Builder {
        return OkHttpClient().newBuilder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .cookieJar(if (!separateJar) JavaNetCookieJar(cookies) else JavaNetCookieJar(CookieManager()))
                .apply {
                    interceptors.forEach {
                        if (it.first is ErrorInterceptor) {
                            if (errorInterceptor) addInterceptor(it.first)
                        } else {
                            if (it.second) addNetworkInterceptor(it.first)
                            else addInterceptor(it.first)
                        }
                    }
                }
    }

    private class UrlGenerator(private val schema: String, private val host: String, private val symbol: String, private val schoolId: String) {

        enum class Site {
            LOGIN, SNP, MESSAGES
        }

        fun generate(type: Site): String {
            return "$schema://${getSubDomain(type)}.$host/$symbol/${if (type == Site.SNP) "$schoolId/" else ""}"
        }

        private fun getSubDomain(type: Site): String {
            return when (type) {
                Site.LOGIN -> "cufs"
                Site.SNP -> "uonetplus-opiekun"
                Site.MESSAGES -> "uonetplus-uzytkownik"
            }
        }
    }
}
