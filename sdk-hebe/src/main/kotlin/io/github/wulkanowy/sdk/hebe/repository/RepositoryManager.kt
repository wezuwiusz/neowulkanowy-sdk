package io.github.wulkanowy.sdk.hebe.repository

import io.github.wulkanowy.sdk.hebe.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.hebe.interceptor.SignInterceptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

internal class RepositoryManager(
    private val keyId: String,
    private val privatePem: String,
    logLevel: HttpLoggingInterceptor.Level,
) {

    private val interceptors: MutableList<Pair<Interceptor, Boolean>> = mutableListOf(
        HttpLoggingInterceptor().setLevel(logLevel) to true,
        ErrorInterceptor() to false,
    )

    fun setInterceptor(interceptor: Interceptor, network: Boolean = false) {
        interceptors.add(0, interceptor to network)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json by lazy {
        Json {
            explicitNulls = false
            encodeDefaults = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    fun getRoutesRepository(): RoutingRulesRepository = RoutingRulesRepository(
        getRetrofitBuilder(isJson = false, signInterceptor = false)
            .baseUrl("https://komponenty.vulcan.net.pl")
            .build()
            .create(),
    )

    fun getStudentRepository(baseUrl: String, schoolId: String): StudentRepository = StudentRepository(
        getRetrofitBuilder(isJson = true, signInterceptor = true)
            .baseUrl("${baseUrl.removeSuffix("/")}/$schoolId/")
            .build()
            .create(),
    )

    internal fun getRegisterRepository(baseUrl: String, symbol: String = ""): RegisterRepository = getRegisterRepository(
        baseUrl = "${baseUrl.removeSuffix("/")}/$symbol",
    )

    private fun getRegisterRepository(baseUrl: String): RegisterRepository = RegisterRepository(
        getRetrofitBuilder(signInterceptor = true)
            .baseUrl("${baseUrl.removeSuffix("/")}/api/mobile/register/")
            .build()
            .create(),
    )

    private fun getRetrofitBuilder(isJson: Boolean = true, signInterceptor: Boolean): Retrofit.Builder = Retrofit
        .Builder()
        .apply {
            when {
                isJson -> addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                else -> addConverterFactory(ScalarsConverterFactory.create())
            }
        }.client(
            OkHttpClient()
                .newBuilder()
                .apply {
                    if (signInterceptor) {
                        addInterceptor(SignInterceptor(keyId, privatePem))
                    }
                    interceptors.forEach {
                        when {
                            it.second -> addNetworkInterceptor(it.first)
                            else -> addInterceptor(it.first)
                        }
                    }
                }.build(),
        )
}
