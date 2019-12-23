package io.github.wulkanowy.sdk.scrapper.mobile

import io.github.wulkanowy.sdk.scrapper.BaseLocalTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MobileTest : BaseLocalTest() {

    private val devicesSnp by lazy {
        getSnpRepo(MobileTest::class.java, "DostepMobilny-filled.html").getRegisteredDevices().blockingGet()
    }

    private val tokenSnp by lazy {
        getSnpRepo(MobileTest::class.java, "Rejestruj.html").getToken().blockingGet()
    }

    private val devicesStudent by lazy {
        getStudentRepo(MobileTest::class.java, "ZarejestrowaneUrzadzenia.json").getRegisteredDevices().blockingGet()
    }

    private val tokenStudent by lazy {
        getStudentRepo(MobileTest::class.java, "RejestracjaUrzadzeniaToken.json").getToken().blockingGet()
    }

    @Test
    fun devicesTest() {
        listOf(devicesSnp, devicesStudent).map {
            it.apply {
                assertEquals(2, size)
                assertEquals("google Android SDK built for x86 (Android 8.1.0)", this[0].name)
                assertEquals("google (Android SDK) built for x86 (Android 8.1.0)", this[1].name)
                assertEquals(getDate(2018, 1, 20), this[1].date)
                assertEquals(321, this[0].id)
            }
        }
    }

    @Test
    fun tokenTest() {
        listOf(tokenSnp, tokenStudent).map {
            it.apply {
                assertEquals("3S1A1B2C", token)
                assertEquals("Default", symbol)
                assertEquals("1234567", pin)
                assertTrue(qrCodeImage.startsWith("iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQCAYAAACAvzbMAAAABmJLR0QA"))
                assertTrue(qrCodeImage.endsWith("IYf8fZKNX0RrMQAAAAASUVORK5CYII="))
            }
        }
    }
}
