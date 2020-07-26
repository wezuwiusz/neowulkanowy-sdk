package io.github.wulkanowy.sdk.scrapper.mobile

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MobileTest : BaseLocalTest() {

    private val devices by lazy {
        runBlocking { getStudentRepo(MobileTest::class.java, "ZarejestrowaneUrzadzenia.json").getRegisteredDevices() }
    }

    private val token by lazy {
        runBlocking { getStudentRepo(MobileTest::class.java, "RejestracjaUrzadzeniaToken.json").getToken() }
    }

    @Test
    fun devicesTest() {
        with(devices) {
            assertEquals("google Android SDK built for x86 (Android 8.1.0)", this[0].name)
            assertEquals("google (Android SDK) built for x86 (Android 8.1.0)", this[1].name)
            assertEquals(getDate(2018, 1, 20), this[1].createDate)
            assertEquals(321, this[0].id)
        }
    }

    @Test
    fun getDevice_full() {
        with(devices[0]) {
            assertEquals(321, id)
            assertEquals("google Android SDK built for x86 (Android 8.1.0)", name)
            assertEquals("374d6203-dc0e-4299-8ca1-14b01e499d22", deviceId)
            assertEquals(getDate(2018, 1, 20, 22, 35, 30), createDate)
            assertEquals(getDate(2018, 1, 20, 22, 35, 31), modificationDate)
        }
    }

    @Test
    fun getDevice_nulls() {
        with(devices[2]) {
            assertEquals(214, id)
            assertEquals(null, name)
            assertEquals(null, deviceId)
            assertEquals(getDate(2020, 4, 9, 23, 35, 7), createDate)
            assertEquals(getDate(2020, 4, 9, 23, 35, 21), modificationDate)
        }
    }

    @Test
    fun tokenTest() {
        with(token) {
            assertEquals("3S1A1B2C", token)
            assertEquals("Default", symbol)
            assertEquals("1234567", pin)
            assertTrue(qrCodeImage.startsWith("iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQCAYAAACAvzbMAAAABmJLR0QA"))
            assertTrue(qrCodeImage.endsWith("IYf8fZKNX0RrMQAAAAASUVORK5CYII="))
        }
    }
}
