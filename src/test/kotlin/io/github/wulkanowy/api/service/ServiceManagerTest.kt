package io.github.wulkanowy.api.service

import io.github.wulkanowy.api.ApiException
import io.github.wulkanowy.api.BaseTest
import io.github.wulkanowy.api.notes.NotesResponse
import io.reactivex.observers.TestObserver
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import java.io.IOException

class ServiceManagerTest : BaseTest() {

    @Test
    fun interceptorTest() {
        val manager = ServiceManager(HttpLoggingInterceptor.Level.NONE,
                "http", "fakelog.localhost:3000", "default", "email", "password",
                "schoolId", "studentId", "diaryId"
        )
        manager.setInterceptor(Interceptor {
            throw ApiException("Test")
        })

        val notes = manager.getSnpService().getNotes()
        val observer = TestObserver<NotesResponse>()
        notes.subscribe(observer)
        observer.assertTerminated()
        observer.assertNotComplete()
        observer.assertError(ApiException::class.java)
    }

    @Test
    fun interceptorTest_prepend() {
        val manager = ServiceManager(HttpLoggingInterceptor.Level.NONE,
                "http", "fakelog.localhost:3000", "default", "email", "password",
                "schoolId", "studentId", "diaryId"
        )
        manager.setInterceptor(Interceptor {
            throw IOException("Test")
        })
        manager.setInterceptor(Interceptor {
            throw ApiException("Test")
        }, 0)

        val notes = manager.getSnpService().getNotes()
        val observer = TestObserver<NotesResponse>()
        notes.subscribe(observer)
        observer.assertTerminated()
        observer.assertNotComplete()
        observer.assertError(ApiException::class.java)
    }
}
