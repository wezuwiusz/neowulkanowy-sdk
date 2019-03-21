package io.github.wulkanowy.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import io.github.wulkanowy.api.grades.DateDeserializer
import io.github.wulkanowy.api.grades.GradeDate
import io.github.wulkanowy.api.interceptor.ErrorInterceptor
import io.github.wulkanowy.api.interceptor.NotLoggedInErrorInterceptor
import io.github.wulkanowy.api.repository.StudentAndParentRepository
import io.github.wulkanowy.api.repository.StudentRepository
import io.github.wulkanowy.api.service.StudentAndParentService
import io.github.wulkanowy.api.service.StudentService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

abstract class BaseLocalTest : BaseTest() {

    val server = MockWebServer()
    val jsonParser = JsonParser()

    @After
    fun tearDown() {
        server.shutdown()
    }

    fun getSnpRepo(testClass: Class<*>, fixture: String, loginType: Api.LoginType = Api.LoginType.STANDARD): StudentAndParentRepository {
        server.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        return StudentAndParentRepository(getService(StudentAndParentService::class.java, server.url("/").toString(), true, true, true, loginType))
    }

    open fun getStudentRepo(testClass: Class<*>, fixture: String, loginType: Api.LoginType = Api.LoginType.STANDARD): StudentRepository {
        server.enqueue(MockResponse().setBody(testClass.getResource(fixture).readText()))
        return StudentRepository(getService(StudentService::class.java, server.url("/").toString(), false, true, true, loginType))
    }

    fun <T> getService(
        service: Class<T>,
        url: String = this.server.url("/").toString(),
        html: Boolean = true,
        errorInterceptor: Boolean = true,
        noLoggedInInterceptor: Boolean = true,
        loginType: Api.LoginType = Api.LoginType.STANDARD
    ): T {
        return Retrofit.Builder()
            .client(OkHttpClient.Builder()
                .apply {
                    if (errorInterceptor) addInterceptor(ErrorInterceptor())
                    if (noLoggedInInterceptor) addInterceptor(NotLoggedInErrorInterceptor(loginType))
                }
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(if (!html) GsonConverterFactory.create(GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(GradeDate::class.java, DateDeserializer(GradeDate.DATE_FORMAT, GradeDate::class.java))
                .create()) else JspoonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(url)
            .build()
            .create(service)
    }
}
