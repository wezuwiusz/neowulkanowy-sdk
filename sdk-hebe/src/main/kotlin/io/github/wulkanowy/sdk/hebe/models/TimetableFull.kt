package io.github.wulkanowy.sdk.hebe.models

data class TimetableFull(
    val lessons: List<Lesson>,
    val headers: List<TimetableHeader>,
    val changes: List<TimetableChange>,
)
