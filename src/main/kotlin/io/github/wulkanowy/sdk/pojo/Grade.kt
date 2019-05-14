package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Grade(
        val subject: String,
        val entry: String,
        val value: Double,
        val modifier: Double,
        val weight: String,
        val weightValue: Double,
        val comment: String?,
        val symbol: String?,
        val description: String,
        val color: String,
        val teacher: String,
        val date: LocalDate

//        val counter: Int?,
//        val denominator: Int?,
//        val creationDate: Int,
//        val modificationDate: Int
)
