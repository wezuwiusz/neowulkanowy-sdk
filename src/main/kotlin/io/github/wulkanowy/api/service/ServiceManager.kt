package io.github.wulkanowy.api.service

import RxJava2ReauthCallAdapterFactory
import io.github.wulkanowy.api.auth.NotLoggedInException
import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.StudentAndParentInterceptor
import io.github.wulkanowy.api.repository.LoginRepository
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class ServiceManager(
        private val logLevel: HttpLoggingInterceptor.Level,
        private val schema: String,
        private val host: String,
        private val symbol: String,
        private val email: String,
        private val password: String,
        private val schoolId: String,
        private val studentId: String,
        private val diaryId: String
) {

    private val cookies by lazy {
        CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
    }

    private val url by lazy {
        UrlGenerator(schema, host, symbol, schoolId)
    }

    fun getLoginService(): LoginService {
        if (email.isBlank() || password.isBlank()) throw NotLoggedInException("Email or/and password are not set")
        return getRetrofit(getClientBuilder(), url.generate(UrlGenerator.Site.LOGIN), false).build()
                .create(LoginService::class.java)
    }

    fun getSnpService(withLogin: Boolean = true, interceptor: Boolean = true): StudentAndParentService {
        if (withLogin && schoolId.isBlank()) throw NotLoggedInException("School id is not set")

        val client = getClientBuilder()
        if (interceptor) {
            if (diaryId.isBlank() || studentId.isBlank()) throw NotLoggedInException("Student or/and diaryId id are not set")
            client.addInterceptor(StudentAndParentInterceptor(cookies, schema, host, diaryId, studentId))
        }

        return getRetrofit(client, url.generate(UrlGenerator.Site.SNP), withLogin).build()
                .create(StudentAndParentService::class.java)
    }

    fun getMessagesService(): MessagesService {
        return getRetrofit(getClientBuilder(), url.generate(UrlGenerator.Site.MESSAGES), true, true).build()
                .create(MessagesService::class.java)
    }

    private fun getRetrofit(client: OkHttpClient.Builder, baseUrl: String, login: Boolean = true, gson: Boolean = false): Retrofit.Builder {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(if (gson) GsonConverterFactory.create() else JspoonConverterFactory.create())
                .addCallAdapterFactory(if (!login) RxJava2CallAdapterFactory.create() else
                    RxJava2ReauthCallAdapterFactory.create(
                            LoginRepository(schema, host, symbol, getLoginService()).login(email, password).toFlowable(),
                            { it is NotLoggedInException }
                    )
                )
    }

    private fun getClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient().newBuilder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .cookieJar(JavaNetCookieJar(cookies))
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(ErrorInterceptor())
    }

    private class UrlGenerator(private val schema: String, private val host: String, private val symbol: String, private val schoolId: String) {

        enum class Site {
            LOGIN, SNP, MESSAGES
        }

        fun generate(type: Site): String {
            return "$schema://${getSubDomain(type)}.$host/$symbol/${if (type == Site.SNP) schoolId else ""}"
        }

        private fun getSubDomain(type: Site): String {
            return when(type) {
                Site.LOGIN -> "cufs"
                Site.SNP -> "uonetplus-opiekun"
                Site.MESSAGES -> "uonetplus-uzytkownik"
            }
        }
    }
}
