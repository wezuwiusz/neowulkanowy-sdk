package io.github.wulkanowy.api.service

import RxJava2ReauthCallAdapterFactory
import com.google.gson.GsonBuilder
import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.OkHttpClientBuilderFactory
import io.github.wulkanowy.api.grades.DateDeserializer
import io.github.wulkanowy.api.grades.GradeDate
import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.NotLoggedInErrorInterceptor
import io.github.wulkanowy.api.interceptor.StudentAndParentInterceptor
import io.github.wulkanowy.api.interceptor.UserAgentInterceptor
import io.github.wulkanowy.api.login.LoginHelper
import io.github.wulkanowy.api.login.NotLoggedInException
import io.github.wulkanowy.api.register.SendCertificateResponse
import io.reactivex.Flowable
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit.SECONDS

class ServiceManager(
    private val okHttpClientBuilderFactory: OkHttpClientBuilderFactory,
    logLevel: HttpLoggingInterceptor.Level,
    private val loginType: Api.LoginType,
    private val schema: String,
    private val host: String,
    private val symbol: String,
    private val email: String,
    private val password: String,
    private val schoolSymbol: String,
    private val studentId: Int,
    private val diaryId: Int,
    androidVersion: String,
    buildTag: String
) {

    private val cookies by lazy {
        CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
    }

    private val loginHelper by lazy {
        LoginHelper(loginType, schema, host, symbol, cookies, getLoginService())
    }

    val urlGenerator by lazy {
        UrlGenerator(schema, host, symbol, schoolSymbol)
    }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf(
        Pair(HttpLoggingInterceptor().setLevel(logLevel), true),
        Pair(ErrorInterceptor(), false),
        Pair(NotLoggedInErrorInterceptor(loginType), false),
        Pair(UserAgentInterceptor(androidVersion, buildTag), false)
    )

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false, index: Int = -1) {
        if (index == -1) interceptors.add(Pair(interceptor, network))
        else interceptors.add(index, Pair(interceptor, network))
    }

    fun getCookieManager(): CookieManager {
        return cookies
    }

    fun getLoginService(): LoginService {
        if (email.isBlank() && password.isBlank()) throw ApiException("Email and password are not set")
        if (email.isBlank()) throw ApiException("Email is not set")
        if (password.isBlank()) throw ApiException("Password is not set")
        return getRetrofit(getClientBuilder(loginIntercept = false), urlGenerator.generate(UrlGenerator.Site.LOGIN), false).create()
    }

    fun getRegisterService(): RegisterService {
        return getRetrofit(getClientBuilder(errIntercept = false, loginIntercept = false, separateJar = true),
            urlGenerator.generate(UrlGenerator.Site.LOGIN),
            false
        ).create()
    }

    fun getStudentService(withLogin: Boolean = true, interceptor: Boolean = true): StudentService {
        if (withLogin && schoolSymbol.isBlank()) throw ApiException("School id is not set")

        val client = getClientBuilder()
        if (interceptor) {
            if (0 == diaryId || 0 == studentId) throw ApiException("Student or/and diaryId id are not set")
            client.addInterceptor(StudentAndParentInterceptor(cookies, schema, host, diaryId, studentId))
        }

        return getRetrofit(client, urlGenerator.generate(UrlGenerator.Site.STUDENT), withLogin, true).create()
    }

    fun getSnpService(withLogin: Boolean = true, interceptor: Boolean = true): StudentAndParentService {
        if (withLogin && schoolSymbol.isBlank()) throw ApiException("School id is not set")

        val client = getClientBuilder(loginIntercept = withLogin)
        if (interceptor) {
            if (0 == diaryId || 0 == studentId) throw ApiException("Student or/and diaryId id are not set")
            client.addInterceptor(StudentAndParentInterceptor(cookies, schema, host, diaryId, studentId))
        }

        return getRetrofit(client, urlGenerator.generate(UrlGenerator.Site.SNP), withLogin).create()
    }

    fun getMessagesService(): MessagesService {
        return getRetrofit(getClientBuilder(), urlGenerator.generate(UrlGenerator.Site.MESSAGES), login = true, gson = true).create()
    }

    fun getHomepageService(): HomepageService {
        return getRetrofit(getClientBuilder(), urlGenerator.generate(UrlGenerator.Site.HOME), login = true, gson = true).create()
    }

    private fun getRetrofit(client: OkHttpClient.Builder, baseUrl: String, login: Boolean = true, gson: Boolean = false): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(if (gson) GsonConverterFactory.create(GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .registerTypeAdapter(GradeDate::class.java, DateDeserializer(GradeDate::class.java))
                .create()) else JspoonConverterFactory.create())
            .addCallAdapterFactory(if (!login) RxJava2CallAdapterFactory.create() else
                RxJava2ReauthCallAdapterFactory.create(
                    getLoginHelper(),
                    { it is NotLoggedInException }
                )
            ).build()
    }

    private fun getClientBuilder(errIntercept: Boolean = true, loginIntercept: Boolean = true, separateJar: Boolean = false): OkHttpClient.Builder {
        return okHttpClientBuilderFactory.create()
            .callTimeout(25, SECONDS)
            .cookieJar(if (!separateJar) JavaNetCookieJar(cookies) else JavaNetCookieJar(CookieManager()))
            .apply {
                interceptors.forEach {
                    if (it.first is ErrorInterceptor || it.first is NotLoggedInErrorInterceptor) {
                        if (it.first is NotLoggedInErrorInterceptor && loginIntercept) addInterceptor(it.first)
                        if (it.first is ErrorInterceptor && errIntercept) addInterceptor(it.first)
                    } else {
                        if (it.second) addNetworkInterceptor(it.first)
                        else addInterceptor(it.first)
                    }
                }
            }
    }

    private fun getLoginHelper(): Flowable<SendCertificateResponse> {
        return loginHelper
            .login(email, password)
            .toFlowable()
            .share()
    }

    class UrlGenerator(private val schema: String, private val host: String, var symbol: String, var schoolId: String) {

        enum class Site {
            LOGIN, HOME, SNP, STUDENT, MESSAGES
        }

        fun generate(type: Site): String {
            return "$schema://${getSubDomain(type)}.$host/$symbol/${if (type == Site.SNP || type == Site.STUDENT) "$schoolId/" else ""}"
        }

        private fun getSubDomain(type: Site): String {
            return when (type) {
                Site.LOGIN -> "cufs"
                Site.HOME -> "uonetplus"
                Site.SNP -> "uonetplus-opiekun"
                Site.STUDENT -> "uonetplus-uczen"
                Site.MESSAGES -> "uonetplus-uzytkownik"
            }
        }
    }
}
