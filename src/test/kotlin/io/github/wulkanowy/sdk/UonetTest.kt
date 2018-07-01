package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.dictionaries.DictionariesResponse
import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.LogResponse
import io.github.wulkanowy.sdk.register.StudentsResponse
import io.github.wulkanowy.sdk.repository.MobileRepository
import io.github.wulkanowy.sdk.repository.RegisterRepository
import junit.framework.TestCase.assertEquals
import org.junit.Test
import rx.observers.TestSubscriber

const val HOST = "https://api.fakelog.cf"
const val SYMBOL = "Default"
const val DEVICE_NAME = "Wulkanowy#client"
const val TOKEN = "FK100000"
const val PIN = "999999"

class UonetTest {

    @Test fun registerTest() {
        // RegisterRepository
        val register = RegisterRepository(HOST, SYMBOL)

        val certificate = register.getCertificate(TOKEN, PIN, DEVICE_NAME)
        val certSubscriber = TestSubscriber<CertificateResponse>()
        certificate.subscribe(certSubscriber)
        certSubscriber.assertCompleted()
        certSubscriber.assertNoErrors()
        assertEquals(false, certSubscriber.onNextEvents[0].isError)

        val tokenCrt = certSubscriber.onNextEvents[0].tokenCert

        register.signature = tokenCrt.certificatePfx
        register.certificate = tokenCrt.certificateKey

        val pupils = register.getPupils()
        val pupilSubscriber = TestSubscriber<StudentsResponse>()
        pupils.subscribe(pupilSubscriber)
        pupilSubscriber.assertCompleted()
        pupilSubscriber.assertNoErrors()
        assertEquals("Ok", pupilSubscriber.onNextEvents[0].status)
        assertEquals(1, pupilSubscriber.onNextEvents[0].students.size)

        val student = pupilSubscriber.onNextEvents[0].students[0]

        // MobileRepository
        val mobile = MobileRepository(HOST, SYMBOL, register.signature, register.certificate, student.reportingUnitSymbol)

        val start = mobile.logStart()
        val startSubscriber = TestSubscriber<LogResponse>()
        start.subscribe(startSubscriber)
        startSubscriber.assertCompleted()
        startSubscriber.assertNoErrors()
        assertEquals("Ok", startSubscriber.onNextEvents[0].status)

        val dictionaries = mobile.getDictionaries(student.userLoginId, student.classificationPeriodId, student.classId)
        val dictionariesSubscriber = TestSubscriber<DictionariesResponse>()
        dictionaries.subscribe(dictionariesSubscriber)
        dictionariesSubscriber.assertCompleted()
        dictionariesSubscriber.assertNoErrors()
        assertEquals("Ok", dictionariesSubscriber.onNextEvents[0].status)
    }
}
