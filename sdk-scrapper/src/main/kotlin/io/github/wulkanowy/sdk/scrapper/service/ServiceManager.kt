package io.github.wulkanowy.sdk.scrapper.service

import com.squareup.moshi.Moshi
import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import io.github.wulkanowy.sdk.scrapper.OkHttpClientBuilderFactory
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.TLSSocketFactory
import io.github.wulkanowy.sdk.scrapper.adapter.GradeDateDeserializer
import io.github.wulkanowy.sdk.scrapper.interceptor.AutoLoginInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.HttpErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.StudentCookieInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.UserAgentInterceptor
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URL
import java.security.KeyStore
import java.time.LocalDate
import java.util.concurrent.TimeUnit.SECONDS
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class ServiceManager(
    private val okHttpClientBuilderFactory: OkHttpClientBuilderFactory,
    logLevel: HttpLoggingInterceptor.Level,
    private val loginType: Scrapper.LoginType,
    private val schema: String,
    private val host: String,
    private val symbol: String,
    private val email: String,
    private val password: String,
    private val schoolSymbol: String,
    private val studentId: Int,
    private val diaryId: Int,
    private val schoolYear: Int,
    emptyCookieJarIntercept: Boolean,
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
        HttpLoggingInterceptor().setLevel(logLevel) to true,
        ErrorInterceptor() to false,
        AutoLoginInterceptor(loginType, cookies, emptyCookieJarIntercept) { loginHelper.login(email, password) } to false,
        UserAgentInterceptor(androidVersion, buildTag) to false,
        HttpErrorInterceptor() to false
    )

    private val trustManager: X509TrustManager by lazy {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as? KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers: $trustManagers")
        }
        trustManagers[0] as X509TrustManager
    }

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false) {
        interceptors.add(0, interceptor to network)
    }

    fun getCookieManager() = cookies

    fun getLoginService(): LoginService {
        if (email.isBlank() && password.isBlank()) throw ScrapperException("Email and password are not set")
        if (email.isBlank()) throw ScrapperException("Email is not set")
        if (password.isBlank()) throw ScrapperException("Password is not set")
        return getRetrofit(getClientBuilder(loginIntercept = false), urlGenerator.generate(UrlGenerator.Site.LOGIN), false).create()
    }

    fun getAccountService(): AccountService {
        return getRetrofit(getClientBuilder(errIntercept = false, loginIntercept = false, separateJar = true),
            urlGenerator.generate(UrlGenerator.Site.LOGIN), false).create()
    }

    fun getRegisterService(): RegisterService {
        return getRetrofit(getClientBuilder(errIntercept = false, loginIntercept = false, separateJar = true),
            urlGenerator.generate(UrlGenerator.Site.LOGIN),
            false
        ).create()
    }

    fun getStudentService(withLogin: Boolean = true, studentInterceptor: Boolean = true): StudentService {
        return getRetrofit(
            client = prepareStudentService(withLogin, studentInterceptor),
            baseUrl = urlGenerator.generate(UrlGenerator.Site.STUDENT),
            gson = true
        ).create()
    }

    private fun prepareStudentService(withLogin: Boolean, studentInterceptor: Boolean): OkHttpClient.Builder {
        if (withLogin && schoolSymbol.isBlank()) throw ScrapperException("School id is not set")

        val client = getClientBuilder(loginIntercept = withLogin)
        if (studentInterceptor) {
            if (0 == diaryId || 0 == studentId) throw ScrapperException("Student or/and diaryId id are not set")

            client.addInterceptor(StudentCookieInterceptor(cookies, schema, host, diaryId, studentId, when (schoolYear) {
                0 -> if (LocalDate.now().monthValue < 9) LocalDate.now().year - 1 else LocalDate.now().year // fallback
                else -> schoolYear
            }))
        }
        return client
    }

    fun getMessagesService(withLogin: Boolean = true): MessagesService {
        return getRetrofit(getClientBuilder(loginIntercept = withLogin), urlGenerator.generate(UrlGenerator.Site.MESSAGES), gson = true).create()
    }

    fun getHomepageService(): HomepageService {
        return getRetrofit(getClientBuilder(), urlGenerator.generate(UrlGenerator.Site.HOME), gson = true).create()
    }

    private fun getRetrofit(client: OkHttpClient.Builder, baseUrl: String, gson: Boolean = false) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(if (gson) MoshiConverterFactory.create(Moshi.Builder()
            .add(CustomDateAdapter())
            .add(GradeDateDeserializer())
            .build()
        ) else JspoonConverterFactory.create())
        .build()

    private fun getClientBuilder(
        errIntercept: Boolean = true,
        loginIntercept: Boolean = true,
        separateJar: Boolean = false
    ) = okHttpClientBuilderFactory.create()
        .connectTimeout(60, SECONDS)
        .callTimeout(60, SECONDS)
        .writeTimeout(60, SECONDS)
        .readTimeout(60, SECONDS)
        .apply {
            when (host) {
                "edu.gdansk.pl",
                "edu.lublin.eu",
                "eduportal.koszalin.pl",
                "vulcan.net.pl" -> sslSocketFactory(TLSSocketFactory(), trustManager)
            }
        }
        .cookieJar(if (!separateJar) JavaNetCookieJar(cookies) else JavaNetCookieJar(CookieManager()))
        .apply {
            interceptors.forEach {
                if (it.first is ErrorInterceptor || it.first is AutoLoginInterceptor) {
                    if (it.first is AutoLoginInterceptor && loginIntercept) addInterceptor(it.first)
                    if (it.first is ErrorInterceptor && errIntercept) addInterceptor(it.first)
                } else {
                    if (it.second) addNetworkInterceptor(it.first)
                    else addInterceptor(it.first)
                }
            }
        }

    class UrlGenerator(private val schema: String, private val host: String, var symbol: String, var schoolId: String) {

        constructor(url: URL, symbol: String, schoolId: String) : this(url.protocol, url.host, symbol, schoolId)

        enum class Site {
            BASE, LOGIN, HOME, SNP, STUDENT, MESSAGES
        }

        fun generate(type: Site): String {
            if (type == Site.BASE) return "$schema://$host"
            return "$schema://${getSubDomain(type)}.$host/$symbol/${if (type == Site.SNP || type == Site.STUDENT) "$schoolId/" else ""}"
        }

        private fun getSubDomain(type: Site): String {
            return when (type) {
                Site.LOGIN -> "cufs"
                Site.HOME -> "uonetplus"
                Site.SNP -> "uonetplus-opiekun"
                Site.STUDENT -> "uonetplus-uczen"
                Site.MESSAGES -> "uonetplus-uzytkownik"
                else -> "unknow"
            }
        }
    }
}
