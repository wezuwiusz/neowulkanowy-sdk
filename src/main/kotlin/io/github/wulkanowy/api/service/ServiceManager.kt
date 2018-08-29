package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.auth.NotLoggedInException
import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.LoginInterceptor
import io.github.wulkanowy.api.interceptor.StudentAndParentInterceptor
import io.github.wulkanowy.api.repository.LoginRepository
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.CookieManager
import java.net.CookiePolicy

class ServiceManager(
        private val logLevel: HttpLoggingInterceptor.Level,
        private val holdSession: Boolean,
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

    private val loginInterceptor by lazy {
        if (email.isBlank() || password.isBlank()) throw NotLoggedInException("Email or/and password are not set")
        LoginInterceptor(LoginRepository(schema, host, symbol, getRetrofit(getClientBuilder(), "cufs", "$symbol/")
                .create(LoginService::class.java)), holdSession, email, password)
    }

    fun getLoginService(): LoginService {
        if (email.isBlank() || password.isBlank()) throw NotLoggedInException("Email or/and password are not set")
        return getRetrofit(getClientBuilder(), "cufs", "$symbol/").create(LoginService::class.java)
    }

    private val studentAndParentInterceptor by lazy {
        if (diaryId.isBlank() || studentId.isBlank()) throw NotLoggedInException("Student or/and diaryId id are not set")
        StudentAndParentInterceptor(cookies, schema, host, diaryId, studentId)
    }

    fun getSnpService(withLogin: Boolean = true, interceptor: Boolean = true): StudentAndParentService {
        val client = getClientBuilder()

        if (withLogin) {
            if (schoolId.isBlank()) throw NotLoggedInException("School id is not set")
            client.addInterceptor(loginInterceptor)
        }

        if (interceptor) client.addInterceptor(studentAndParentInterceptor)

        return getRetrofit(client, "uonetplus-opiekun", "$symbol/$schoolId/").create(StudentAndParentService::class.java)
    }

    private fun getRetrofit(client: OkHttpClient.Builder, subDomain: String, urlAppend: String): Retrofit {
        return Retrofit.Builder()
                .baseUrl("$schema://$subDomain.$host/$urlAppend")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JspoonConverterFactory.create())
                .client(client.build())
                .build()
    }

    private fun getClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient().newBuilder()
                .cookieJar(JavaNetCookieJar(cookies))
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(ErrorInterceptor())
    }
}
