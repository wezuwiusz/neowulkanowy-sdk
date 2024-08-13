package io.github.wulkanowy.sdk.prometheus.service

import io.github.wulkanowy.sdk.prometheus.CookieJarCabinet
import io.github.wulkanowy.sdk.prometheus.OkHttpClientBuilderFactory
import io.github.wulkanowy.sdk.prometheus.adapter.ObjectSerializer
import io.github.wulkanowy.sdk.prometheus.exception.PrometheusException
import io.github.wulkanowy.sdk.prometheus.interceptor.UserAgentInterceptor
import io.github.wulkanowy.sdk.prometheus.login.LoginHelper
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit.SECONDS

class ServiceManager(
    private val okHttpClientBuilderFactory: OkHttpClientBuilderFactory,
    private val cookieJarCabinet: CookieJarCabinet,
    private val baseURL: String,
    private val username: String,
    private val password: String,
) {
    companion object {
        private const val TIMEOUT_IN_SECONDS = 30L
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

    private val loginHelper by lazy {
        LoginHelper(
            cookieJarCabinet = cookieJarCabinet,
            api = getLoginService(),
        )
    }

    suspend fun userLogin() = loginHelper.login(username, password)

    private fun getLoginService(): LoginService {
        if (username.isBlank() && password.isBlank()) throw PrometheusException("Email and password are not set")
        if (username.isBlank()) throw PrometheusException("Email is not set")
        if (password.isBlank()) throw PrometheusException("Password is not set")
        return getRetrofit(getClientBuilder(loginIntercept = false), baseURL, false).create()
    }

    fun getInfoService(): InfoService = getRetrofit(getClientBuilder(loginIntercept = false), baseURL, true).create()

    private fun getRetrofit(client: OkHttpClient.Builder, baseUrl: String, json: Boolean = false) = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .client(client.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            this.json.asConverterFactory("application/json".toMediaType()),
        ).build()

    private fun getClientBuilder(
        errIntercept: Boolean = true,
        loginIntercept: Boolean = true,
        separateJar: Boolean = false,
    ): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return okHttpClientBuilderFactory
            .create()
            .connectTimeout(TIMEOUT_IN_SECONDS, SECONDS)
            .callTimeout(TIMEOUT_IN_SECONDS, SECONDS)
            .writeTimeout(TIMEOUT_IN_SECONDS, SECONDS)
            .readTimeout(TIMEOUT_IN_SECONDS, SECONDS)
            .addInterceptor(logging)
            .addInterceptor(UserAgentInterceptor())
            .cookieJar(
                when {
                    separateJar -> JavaNetCookieJar(cookieJarCabinet.alternativeCookieManager)
                    else -> JavaNetCookieJar(cookieJarCabinet.userCookieManager)
                },
            )
    }
}
