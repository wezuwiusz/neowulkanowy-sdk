package io.github.wulkanowy.sdk.scrapper.attendance

import org.junit.Test

import org.junit.Assert.*

class AttendanceCategoryTest {

    @Test
    fun getCategoryById() {
        assertEquals(AttendanceCategory.PRESENCE, AttendanceCategory.getCategoryById(1))
        assertEquals(AttendanceCategory.DELETED, AttendanceCategory.getCategoryById(8))
        assertEquals(AttendanceCategory.UNKNOWN, AttendanceCategory.getCategoryById(128))
    }

    @Test
    fun getByName() {
        assertEquals(AttendanceCategory.PRESENCE, AttendanceCategory.getCategoryByName("PRESENCE"))
        assertEquals(AttendanceCategory.DELETED, AttendanceCategory.getCategoryByName("DELETED"))
        assertEquals(AttendanceCategory.PRESENCE, AttendanceCategory.getCategoryByName("Obecność"))
        assertEquals(AttendanceCategory.UNKNOWN, AttendanceCategory.getCategoryByName(";laksjdf"))
    }
}
