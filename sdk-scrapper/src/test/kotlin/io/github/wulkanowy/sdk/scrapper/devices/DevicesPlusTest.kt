package io.github.wulkanowy.sdk.scrapper.devices

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class DevicesPlusTest : BaseLocalTest() {
    private val devices by lazy {
        runBlocking {
            getStudentPlusRepo(DevicesPlusTest::class.java, "UrzadzeniaPlus.json")
                .getRegisteredDevices()
        }
    }

    @Test
    fun test() {
        assertEquals(1, devices.size)
        with(devices[0]) {
            assertEquals(1, id)
            assertEquals(LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.ofHours(0)), createDate)
            assertEquals("Wulkanowy", name)
        }
    }
}
