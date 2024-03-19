package io.github.wulkanowy.sdk.scrapper.service

import io.github.wulkanowy.sdk.scrapper.CookieJarCabinet
import io.github.wulkanowy.sdk.scrapper.OkHttpClientBuilderFactory
import io.github.wulkanowy.sdk.scrapper.Scrapper
import io.github.wulkanowy.sdk.scrapper.TLSSocketFactory
import io.github.wulkanowy.sdk.scrapper.adapter.ObjectSerializer
import io.github.wulkanowy.sdk.scrapper.exception.ScrapperException
import io.github.wulkanowy.sdk.scrapper.interceptor.AutoLoginInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.HttpErrorInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.StudentCookieInterceptor
import io.github.wulkanowy.sdk.scrapper.interceptor.UserAgentInterceptor
import io.github.wulkanowy.sdk.scrapper.login.LoginHelper
import io.github.wulkanowy.sdk.scrapper.login.UrlGenerator
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.security.KeyStore
import java.time.LocalDate
import java.util.concurrent.TimeUnit.SECONDS
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

internal class ServiceManager(
    private val okHttpClientBuilderFactory: OkHttpClientBuilderFactory,
    private val cookieJarCabinet: CookieJarCabinet,
    logLevel: HttpLoggingInterceptor.Level,
    private val loginType: Scrapper.LoginType,
    private val schema: String,
    private val host: String,
    private val domainSuffix: String,
    private val symbol: String,
    private val email: String,
    private val password: String,
    private val schoolId: String,
    private val studentId: Int,
    private val diaryId: Int,
    private val kindergartenDiaryId: Int,
    private val schoolYear: Int,
    isEduOneStudent: (Boolean) -> Unit = {},
    emptyCookieJarIntercept: Boolean,
    androidVersion: String,
    buildTag: String,
    userAgentTemplate: String,
) {

    val urlGenerator by lazy {
        UrlGenerator(
            schema = schema,
            host = host,
            domainSuffix = domainSuffix,
            symbol = symbol,
            schoolId = schoolId,
        )
    }

    private val loginHelper by lazy {
        LoginHelper(
            loginType = loginType,
            schema = schema,
            host = host,
            domainSuffix = domainSuffix,
            symbol = symbol,
            cookieJarCabinet = cookieJarCabinet,
            api = getLoginService(),
            urlGenerator = urlGenerator,
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    val json by lazy {
        Json {
            explicitNulls = false
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            serializersModule = SerializersModule {
                contextual(ObjectSerializer)
            }
        }
    }

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf(
        HttpLoggingInterceptor().setLevel(logLevel) to true,
        ErrorInterceptor(cookieJarCabinet) to false,
        AutoLoginInterceptor(
            loginType = loginType,
            cookieJarCabinet = cookieJarCabinet,
            emptyCookieJarIntercept = emptyCookieJarIntercept,
            notLoggedInCallback = { loginHelper.login(email, password) },
            fetchModuleCookies = { site, isSuccessRequired -> loginHelper.loginModule(site, isSuccessRequired) },
            isEduOneStudent = isEduOneStudent,
            urlGenerator = urlGenerator,
        ) to false,
        UserAgentInterceptor(androidVersion, buildTag, userAgentTemplate) to false,
        HttpErrorInterceptor() to false,
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

    companion object {
        private const val TIMEOUT_IN_SECONDS = 30L
    }

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false) {
        interceptors.add(0, interceptor to network)
    }

    fun getSymbolService(): SymbolService {
        return getRetrofit(
            client = getClientBuilder(errIntercept = true, loginIntercept = false, separateJar = true),
            baseUrl = urlGenerator.generateBase(UrlGenerator.Site.HOME),
            json = false,
        ).create()
    }

    fun getLoginService(): LoginService {
        if (email.isBlank() && password.isBlank()) throw ScrapperException("Email and password are not set")
        if (email.isBlank()) throw ScrapperException("Email is not set")
        if (password.isBlank()) throw ScrapperException("Password is not set")
        return getRetrofit(getClientBuilder(loginIntercept = false), urlGenerator.generate(UrlGenerator.Site.LOGIN), false).create()
    }

    fun getAccountService(): AccountService {
        return getRetrofit(
            client = getClientBuilder(errIntercept = false, loginIntercept = false, separateJar = true),
            baseUrl = urlGenerator.generate(UrlGenerator.Site.LOGIN),
            json = false,
        ).create()
    }

    fun getRegisterService(): RegisterService {
        return getRetrofit(
            client = getClientBuilder(errIntercept = true, loginIntercept = false, separateJar = true),
            baseUrl = urlGenerator.generate(UrlGenerator.Site.LOGIN),
            json = false,
        ).create()
    }

    fun getStudentService(withLogin: Boolean = true, studentInterceptor: Boolean = true): StudentService {
        return getRetrofit(
            client = prepareStudentService(withLogin, studentInterceptor),
            baseUrl = urlGenerator.generate(UrlGenerator.Site.STUDENT),
            json = true,
        ).create()
    }

    fun getStudentPlusService(withLogin: Boolean = true, studentInterceptor: Boolean = true): StudentPlusService {
        return getRetrofit(
            client = prepareStudentService(withLogin, studentInterceptor),
            baseUrl = urlGenerator.generate(UrlGenerator.Site.STUDENT_PLUS),
            json = true,
        ).create()
    }

    private fun prepareStudentService(withLogin: Boolean, studentInterceptor: Boolean): OkHttpClient.Builder {
        if (withLogin && schoolId.isBlank()) throw ScrapperException("School id is not set")

        val client = getClientBuilder(loginIntercept = withLogin)
        if (studentInterceptor) {
            if ((0 == diaryId && 0 == kindergartenDiaryId) || 0 == studentId) throw ScrapperException("Student or/and diaryId id are not set")

            client.addInterceptor(
                StudentCookieInterceptor(
                    cookieJarCabinet = cookieJarCabinet,
                    schema = schema,
                    host = host,
                    domainSuffix = domainSuffix,
                    diaryId = diaryId,
                    kindergartenDiaryId = kindergartenDiaryId,
                    studentId = studentId,
                    schoolYear = when (schoolYear) {
                        0 -> if (LocalDate.now().monthValue < 9) LocalDate.now().year - 1 else LocalDate.now().year // fallback
                        else -> schoolYear
                    },
                ),
            )
        }
        return client
    }

    fun getMessagesService(withLogin: Boolean = true): MessagesService {
        return getRetrofit(
            client = getClientBuilder(loginIntercept = withLogin),
            baseUrl = urlGenerator.generate(UrlGenerator.Site.MESSAGES),
            json = true,
        ).create()
    }

    fun getHomepageService(): HomepageService {
        return getRetrofit(getClientBuilder(), urlGenerator.generate(UrlGenerator.Site.HOME), json = true).create()
    }

    private fun getRetrofit(client: OkHttpClient.Builder, baseUrl: String, json: Boolean = false) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            when {
                json -> this.json.asConverterFactory("application/json".toMediaType())
                else -> JspoonConverterFactory.create()
            },
        )
        .build()

    private fun getClientBuilder(
        errIntercept: Boolean = true,
        loginIntercept: Boolean = true,
        separateJar: Boolean = false,
    ) = okHttpClientBuilderFactory.create()
        .connectTimeout(TIMEOUT_IN_SECONDS, SECONDS)
        .callTimeout(TIMEOUT_IN_SECONDS, SECONDS)
        .writeTimeout(TIMEOUT_IN_SECONDS, SECONDS)
        .readTimeout(TIMEOUT_IN_SECONDS, SECONDS)
        .apply {
            when (host) {
                "edu.gdansk.pl",
                "edu.lublin.eu",
                "eduportal.koszalin.pl",
                "vulcan.net.pl",
                -> sslSocketFactory(TLSSocketFactory(), trustManager)
            }
        }
        .cookieJar(
            when {
                separateJar -> JavaNetCookieJar(cookieJarCabinet.alternativeCookieManager)
                else -> JavaNetCookieJar(cookieJarCabinet.userCookieManager)
            },
        )
        .apply {
            interceptors.forEach {
                if (it.first is ErrorInterceptor || it.first is AutoLoginInterceptor) {
                    if (it.first is AutoLoginInterceptor && loginIntercept) addInterceptor(it.first)
                    if (it.first is ErrorInterceptor && errIntercept) addInterceptor(it.first)
                } else {
                    when {
                        it.second -> addNetworkInterceptor(it.first)
                        else -> addInterceptor(it.first)
                    }
                }
            }
        }
}
