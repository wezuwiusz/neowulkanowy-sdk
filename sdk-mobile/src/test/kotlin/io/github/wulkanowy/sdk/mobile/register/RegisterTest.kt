package io.github.wulkanowy.sdk.mobile.register

import io.github.wulkanowy.sdk.mobile.BaseLocalTest
import io.github.wulkanowy.sdk.mobile.repository.RegisterRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.create

class RegisterTest : BaseLocalTest() {

    private val repo by lazy { RegisterRepository(getRetrofit().create()) }

    @Test
    fun getStudents() {
        server.enqueueAndStart("ListaUczniow.json")

        val students = runBlocking { repo.getStudents() }
        assertEquals(2, students.size)
    }
}
