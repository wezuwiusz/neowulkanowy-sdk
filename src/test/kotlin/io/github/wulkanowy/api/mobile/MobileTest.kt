package io.github.wulkanowy.api.mobile

import io.github.wulkanowy.api.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MobileTest : BaseLocalTest() {

    @Test
    fun devicesTest() {
        val devices = getSnpRepo(MobileTest::class.java, "DostepMobilny-filled.html").getRegisteredDevices().blockingGet()

        assertEquals(2, devices.size)
        assertEquals("google Android SDK built for x86 (Android 8.1.0)", devices[0].name)
        assertEquals("google (Android SDK) built for x86 (Android 8.1.0)", devices[1].name)
        assertEquals(getDate(2018, 1, 20), devices[1].date)
        assertEquals(321, devices[0].id)
    }

    @Test
    fun tokenTest() {
        val token = getSnpRepo(MobileTest::class.java, "Rejestruj.html").getToken().blockingGet()

        assertEquals("3S1A1B2C", token.token)
        assertEquals("Default", token.symbol)
        assertEquals("1234567", token.pin)
    }
}
