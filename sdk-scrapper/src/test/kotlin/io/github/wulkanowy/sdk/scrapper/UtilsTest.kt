package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.RecipientType
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun `test parsing simple recipient name`() {
        val recipient = Recipient("", "Arkadiusz Sierpień - P - (000011)").parseName()
        assertEquals("Arkadiusz Sierpień", recipient.name)
        assertEquals("Arkadiusz Sierpień", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing recipient name with multiple dashes`() {
        val recipient = Recipient("", "Renata Przerwa-Tetmajer - P - (000011)").parseName()
        assertEquals("Renata Przerwa-Tetmajer", recipient.name)
        assertEquals("Renata Przerwa-Tetmajer", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing recipient name with initials only`() {
        val recipient = Recipient("", "U R - P - (000011)").parseName()
        assertEquals("U R", recipient.name)
        assertEquals("U R", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing recipient name with numbers`() {
        val recipient = Recipient("", "1 CKP - P - (000011)").parseName()
        assertEquals("1 CKP", recipient.name)
        assertEquals("1 CKP", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing student recipient name`() {
        val recipient = Recipient("", "Jan Kowalski - U - (000011)").parseName()
        assertEquals("Jan Kowalski", recipient.name)
        assertEquals("Jan Kowalski", recipient.studentName)
        assertEquals(RecipientType.STUDENT, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing parent recipient name`() {
        val recipient = Recipient("", "Stanisław Kowalski - R - Jan Kowalski - (000011)").parseName()
        assertEquals("Stanisław Kowalski", recipient.name)
        assertEquals("Jan Kowalski", recipient.studentName)
        assertEquals(RecipientType.PARENT, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }
}
