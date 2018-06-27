package io.github.wulkanowy.sdk

import io.github.wulkanowy.sdk.register.CertificateResponse
import io.github.wulkanowy.sdk.register.StudentsResponse
import junit.framework.TestCase.assertEquals
import org.junit.Test
import rx.observers.TestSubscriber

class UonetTest {

    private val service by lazy { Uonet("https://api.fakelog.cf", "Default") }

    @Test fun registerTest() {
        val certificate = service.getCertificate("FK100000", "999999", "Wulkanowy#client")

        val certSubscriber = TestSubscriber<CertificateResponse>()
        certificate.subscribe(certSubscriber)
        certSubscriber.assertCompleted()
        certSubscriber.assertNoErrors()
        assertEquals(false, certSubscriber.onNextEvents[0].isError)

        val tokenCrt = certSubscriber.onNextEvents[0].tokenCert

        service.signature = tokenCrt.certificatePfx
        service.certificate = tokenCrt.certificateKey

        val pupils = service.getPupils()
        val pupilSubscriber = TestSubscriber<StudentsResponse>()
        pupils.subscribe(pupilSubscriber)
        pupilSubscriber.assertCompleted()
        pupilSubscriber.assertNoErrors()
        assertEquals("Ok", pupilSubscriber.onNextEvents[0].status)
        assertEquals(1, pupilSubscriber.onNextEvents[0].students.size)
    }
}
