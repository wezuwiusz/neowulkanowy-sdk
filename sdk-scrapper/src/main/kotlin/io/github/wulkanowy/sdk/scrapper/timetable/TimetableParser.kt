package io.github.wulkanowy.sdk.scrapper.timetable

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class TimetableParser {

    private companion object {
        const val CLASS_PLANNED = "x-treelabel-ppl"
        const val CLASS_REALIZED = "x-treelabel-rlz"
        const val CLASS_CHANGES = "x-treelabel-zas"
        const val CLASS_MOVED_OR_CANCELED = "x-treelabel-inv"
    }

    fun getTimetable(c: TimetableResponse.TimetableRow.TimetableCell): Timetable? {
        return addLessonDetails(Timetable(c.number, c.start, c.end, c.date), c.td)
    }

    private fun addLessonDetails(lesson: Timetable, td: Element): Timetable? {
        val divs = td.select("div:not([class])")
        val warnElement = td.select(".uwaga-panel").getOrNull(0)

        return when {
            divs.size == 1 -> getLessonInfo(lesson, divs[0])
            divs.size == 2 && divs[1]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) ?: false -> {
                when {
                    divs[1]?.selectFirst("span")?.hasClass(CLASS_PLANNED) == true -> getLessonInfo(lesson, divs[0]).run {
                        val old = getLessonInfo(lesson, divs[1])
                        copy(
                            changes = true,
                            subjectOld = old.subject,
                            teacherOld = old.teacher,
                            roomOld = old.room,
                            info = stripLessonInfo("${getFormattedLessonInfo(info)}, ${old.info}").replace("$subject ", "").capitalize()
                        )
                    }
                    else -> getLessonInfo(lesson, divs[1])
                }
            }
            divs.size == 2 && divs[1]?.selectFirst("span")?.hasClass(CLASS_CHANGES) == true -> getLessonInfo(lesson, divs[1]).run {
                val old = getLessonInfo(lesson, divs[0])
                copy(
                    changes = true,
                    canceled = false,
                    subjectOld = old.subject,
                    teacherOld = old.teacher,
                    roomOld = old.room
                )
            }
            divs.size == 2 && divs[0]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) == true &&
                divs[0]?.selectFirst("span")?.hasClass(CLASS_PLANNED) == true &&
                divs[1]?.selectFirst("span")?.attr("class")?.isEmpty() == true -> {
                getLessonInfo(lesson, divs[1]).run {
                    val old = getLessonInfo(lesson, divs[0])
                    copy(
                        changes = true,
                        canceled = false,
                        subjectOld = old.subject,
                        teacherOld = old.teacher,
                        roomOld = old.room,
                        info = "Poprzednio: ${old.subject} (${old.info})"
                    )
                }
            }
            divs.size == 2 -> getLessonInfo(lesson, divs[0])
            divs.size == 3 -> when { // TODO: refactor this
                divs[0]?.selectFirst("span")?.hasClass(CLASS_CHANGES) == true &&
                    divs[1]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) == true &&
                    divs[2]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) == true -> {
                    getLessonInfo(lesson, divs[0]).run {
                        val old = getLessonInfo(lesson, divs[1])
                        copy(
                            changes = true,
                            canceled = false,
                            subjectOld = old.subject,
                            teacherOld = old.teacher,
                            roomOld = old.room
                        )
                    }
                }
                divs[0]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) == true &&
                    divs[1]?.selectFirst("span")?.hasClass(CLASS_MOVED_OR_CANCELED) == true &&
                    divs[2]?.selectFirst("span")?.hasClass(CLASS_CHANGES) == true -> {
                    getLessonInfo(lesson, divs[2]).run {
                        val old = getLessonInfo(lesson, divs[0])
                        copy(
                            changes = true,
                            canceled = false,
                            subjectOld = old.subject,
                            teacherOld = old.teacher,
                            roomOld = old.room
                        )
                    }
                }
                else -> getLessonInfo(lesson, divs[1])
            }
            else -> null
        }?.let {
            warnElement?.let { warn ->
                if (it.info.isBlank()) it.copy(info = warn.text())
                else it.copy(info = "${it.info}: ${warn.text()}")
            } ?: it
        }
    }

    private fun getLessonInfo(lesson: Timetable, div: Element) = div.select("span").run {
        when {
            size == 3 -> getSimpleLesson(lesson, this)
            size == 4 && last().hasClass(CLASS_REALIZED) -> getSimpleLesson(lesson, this)
            size == 4 -> getGroupLesson(lesson, this)
            size == 5 && first().hasClass(CLASS_CHANGES) && select(".$CLASS_REALIZED").size == 2 -> getSimpleLesson(lesson, this, 1)
            size == 5 && last().hasClass(CLASS_REALIZED) -> getGroupLesson(lesson, this)
            size == 7 -> getSimpleLessonWithReplacement(lesson, this)
            size == 9 -> getGroupLessonWithReplacement(lesson, this)
            else -> lesson
        }
    }

    private fun getSimpleLesson(lesson: Timetable, spans: Elements, infoExtraOffset: Int = 0): Timetable {
        return getLesson(lesson, spans, 0, infoExtraOffset)
    }

    private fun getSimpleLessonWithReplacement(lesson: Timetable, spans: Elements): Timetable {
        return getLessonWithReplacement(lesson, spans)
    }

    private fun getGroupLesson(lesson: Timetable, spans: Elements): Timetable {
        return getLesson(lesson, spans, 1)
    }

    private fun getGroupLessonWithReplacement(lesson: Timetable, spans: Elements): Timetable {
        return getLessonWithReplacement(lesson, spans, 1)
    }

    private fun getLesson(lesson: Timetable, spans: Elements, offset: Int = 0, infoExtraOffset: Int = 0) = lesson.copy(
        subject = getLessonAndGroupInfoFromSpan(spans[0])[0],
        group = getLessonAndGroupInfoFromSpan(spans[0])[1],
        teacher = spans[1 + offset].text(),
        room = spans[2 + offset].text(),
        info = getFormattedLessonInfo(spans.getOrNull(3 + offset + infoExtraOffset)?.text()),
        canceled = spans.first().hasClass(CLASS_MOVED_OR_CANCELED),
        changes = spans.first().hasClass(CLASS_CHANGES)
    )

    private fun getLessonWithReplacement(lesson: Timetable, spans: Elements, o: Int = 0) = lesson.copy(
        subject = getLessonAndGroupInfoFromSpan(spans[3 + o])[0],
        subjectOld = getLessonAndGroupInfoFromSpan(spans[0])[0],
        group = getLessonAndGroupInfoFromSpan(spans[3 + o])[1],
        teacher = spans[4 + o * 2].text(),
        teacherOld = spans[1 + o].text(),
        room = spans[5 + o * 2].text(),
        roomOld = spans[2 + o].text(),
        info = "${getFormattedLessonInfo(spans.last().text())}, poprzednio: ${getLessonAndGroupInfoFromSpan(spans[0])[0]}",
        changes = true
    )

    private fun getFormattedLessonInfo(info: String?) = info?.removeSurrounding("(", ")").orEmpty()

    private fun stripLessonInfo(info: String) = info
        .replace("okienko dla uczniów", "")
        .replace("zmiana organizacji zajęć", "")
        .replace(" ,", "")
        .removePrefix(", ")

    private fun getLessonAndGroupInfoFromSpan(span: Element) = arrayOf(
        span.text().substringBefore(" ["),
        if (span.text().contains("[")) span.text().split(" [").last().removeSuffix("]") else ""
    )
}
