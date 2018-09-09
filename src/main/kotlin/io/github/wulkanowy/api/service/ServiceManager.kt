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
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        cookieManager
    }

    private val loginRepository by lazy {
        if (email.isBlank() || password.isBlank()) throw NotLoggedInException("Email or/and password are not set")
        LoginRepository(schema, host, symbol, getRetrofit(getClientBuilder(), "cufs", "$symbol/", false)
                .addConverterFactory(JspoonConverterFactory.create()).build()
                .create(LoginService::class.java))
    }

    fun getLoginService(): LoginService {
        if (email.isBlank() || password.isBlank()) throw NotLoggedInException("Email or/and password are not set")
        return getRetrofit(getClientBuilder(), "cufs", "$symbol/", false)
                .addConverterFactory(JspoonConverterFactory.create()).build()
                .create(LoginService::class.java)
    }

    private val studentAndParentInterceptor by lazy {
        if (diaryId.isBlank() || studentId.isBlank()) throw NotLoggedInException("Student or/and diaryId id are not set")
        StudentAndParentInterceptor(cookies, schema, host, diaryId, studentId)
    }

    fun getSnpService(withLogin: Boolean = true, interceptor: Boolean = true): StudentAndParentService {
        if (withLogin && schoolId.isBlank()) throw NotLoggedInException("School id is not set")

        val client = getClientBuilder()
        if (interceptor) client.addInterceptor(studentAndParentInterceptor)

        return getRetrofit(client, "uonetplus-opiekun", "$symbol/$schoolId/", withLogin)
                .addConverterFactory(JspoonConverterFactory.create()).build()
                .create(StudentAndParentService::class.java)
    }

    fun getMessagesService(): MessagesService {
        return getRetrofit(getClientBuilder(), "uonetplus-uzytkownik", "$symbol/")
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(MessagesService::class.java)
    }

    private fun getRetrofit(client: OkHttpClient.Builder, subDomain: String, urlAppend: String, login: Boolean = true): Retrofit.Builder {
        return Retrofit.Builder()
                .baseUrl("$schema://$subDomain.$host/$urlAppend")
                .client(client.build())
                .addCallAdapterFactory(if (!login) RxJava2CallAdapterFactory.create() else
                    RxJava2ReauthCallAdapterFactory.create(
                            loginRepository.login(email, password).toFlowable(),
                            { it is NotLoggedInException }
                    )
                )
    }

    private fun getClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient().newBuilder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .cookieJar(JavaNetCookieJar(cookies))
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(ErrorInterceptor())
    }
}
