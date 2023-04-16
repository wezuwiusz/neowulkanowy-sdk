package io.github.wulkanowy.sdk.hebe.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.wulkanowy.sdk.hebe.interceptor.ErrorInterceptor
import io.github.wulkanowy.sdk.hebe.interceptor.SignInterceptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

internal class RepositoryManager(
    private val logLevel: HttpLoggingInterceptor.Level,
    private val privateKey: String,
    private val interceptors: MutableList<Pair<Interceptor, Boolean>>,
    private val baseUrl: String,
    private val schoolSymbol: String,
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val json by lazy {
        Json {
            explicitNulls = false
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    fun getRoutesRepository(): RoutingRulesRepository {
        return RoutingRulesRepository(
            getRetrofitBuilder(interceptors)
                .baseUrl("http://komponenty.vulcan.net.pl")
                .build()
                .create(),
        )
    }

    internal fun getRegisterRepository(baseUrl: String, symbol: String): RegisterRepository = getRegisterRepository(
        "${baseUrl.removeSuffix("/")}/$symbol",
    )

    private fun getRegisterRepository(baseUrl: String): RegisterRepository = RegisterRepository(
        getRetrofitBuilder(interceptors)
            .baseUrl("${baseUrl.removeSuffix("/")}/api/mobile/register/")
            .build()
            .create(),
    )

    @OptIn(ExperimentalSerializationApi::class)
    private fun getRetrofitBuilder(interceptors: MutableList<Pair<Interceptor, Boolean>>): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(
                OkHttpClient().newBuilder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                    .addInterceptor(ErrorInterceptor())
                    .addInterceptor(SignInterceptor(privateKey))
                    .apply {
                        interceptors.forEach {
                            if (it.second) addNetworkInterceptor(it.first)
                            else addInterceptor(it.first)
                        }
                    }
                    .build(),
            )
    }
}
