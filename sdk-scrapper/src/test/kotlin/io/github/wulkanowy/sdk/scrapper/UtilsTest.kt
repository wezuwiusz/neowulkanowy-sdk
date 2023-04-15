package io.github.wulkanowy.sdk.scrapper

import io.github.wulkanowy.sdk.scrapper.messages.Recipient
import io.github.wulkanowy.sdk.scrapper.messages.RecipientType
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun `test parsing simple recipient name`() {
        val recipient = Recipient("", "Arkadiusz Sierpień - P - (000011)").parseName()
        assertEquals("Arkadiusz Sierpień", recipient.userName)
        assertEquals("Arkadiusz Sierpień", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing recipient name with multiple dashes`() {
        val recipient = Recipient("", "Renata Przerwa-Tetmajer - P - (000011)").parseName()
        assertEquals("Renata Przerwa-Tetmajer", recipient.userName)
        assertEquals("Renata Przerwa-Tetmajer", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing recipient name with multiple dashes and spaces`() {
        val recipient = Recipient("", "Kazimierz Przerwa  - Tetmajer - P - (000011)").parseName()
        assertEquals("Kazimierz Przerwa  - Tetmajer", recipient.userName)
        assertEquals("Kazimierz Przerwa  - Tetmajer", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing recipient name with initials only`() {
        val recipient = Recipient("", "U R - P - (000011)").parseName()
        assertEquals("U R", recipient.userName)
        assertEquals("U R", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing recipient name with numbers`() {
        val recipient = Recipient("", "1 CKP - P - (000011)").parseName()
        assertEquals("1 CKP", recipient.userName)
        assertEquals("1 CKP", recipient.studentName)
        assertEquals(RecipientType.EMPLOYEE, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing student recipient name`() {
        val recipient = Recipient("", "Jan Kowalski - U - (000011)").parseName()
        assertEquals("Jan Kowalski", recipient.userName)
        assertEquals("Jan Kowalski", recipient.studentName)
        assertEquals(RecipientType.STUDENT, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing parent recipient name`() {
        val recipient = Recipient("", "Stanisław Kowalski - R - Jan Kowalski - (000011)").parseName()
        assertEquals("Stanisław Kowalski", recipient.userName)
        assertEquals("Jan Kowalski", recipient.studentName)
        assertEquals(RecipientType.PARENT, recipient.type)
        assertEquals("000011", recipient.schoolNameShort)
    }

    @Test
    fun `test parsing system sender name`() {
        val recipient = Recipient("", "System").parseName()
        assertEquals("System", recipient.userName)
        assertEquals("", recipient.studentName)
        assertEquals(RecipientType.UNKNOWN, recipient.type)
        assertEquals("", recipient.schoolNameShort)
    }
}
