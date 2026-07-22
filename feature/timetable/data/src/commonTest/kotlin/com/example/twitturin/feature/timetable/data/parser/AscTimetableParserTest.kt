package com.example.twitturin.feature.timetable.data.parser

import com.example.twitturin.core.domain.util.Result
import com.example.twitturin.feature.timetable.domain.SubjectKind
import com.example.twitturin.feature.timetable.domain.Timetable
import com.example.twitturin.feature.timetable.domain.TimetableCoverage
import com.example.twitturin.feature.timetable.domain.TimetableError
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Fixtures are trimmed excerpts of a real aSc Timetables 2012 XML export (the format the app
 * imports), kept structurally faithful: same element/attribute names, the 6-char Mon..Sat day
 * bitmask, the "(lec)/(prac)" subject-name suffix convention, and a teacherless placeholder
 * lesson mirroring how the real export encodes institution-wide HOLIDAY/Exam-Session blocks.
 */
class AscTimetableParserTest {

    @Test
    fun `parses periods, subjects and occurrences from a weekly-recurring export`() {
        val timetable = parseOrFail(SINGLE_WEEK_SAMPLE)

        assertEquals(3, timetable.subjects.size)
        assertEquals(TimetableCoverage.WEEKLY_RECURRING, timetable.coverage)

        // 1 lecture occurrence + 6 (Mon..Sat) holiday occurrences at period 1 + 6 more at period 2.
        assertEquals(13, timetable.occurrences.size)

        val lecture = timetable.occurrences.single { it.subjectId == "SUBJ_LEC" }
        assertEquals(DayOfWeek.MONDAY, lecture.dayOfWeek)
        assertEquals(1, lecture.period.number)
        assertEquals(listOf("T1"), lecture.teacherIds)
        assertEquals(listOf("R1"), lecture.roomIds)
        assertTrue(lecture.isInstructional)

        val holidayOccurrences = timetable.occurrences.filter { it.subjectId == "SUBJ_HOLIDAY" }
        assertEquals(12, holidayOccurrences.size)
        assertTrue(holidayOccurrences.none { it.isInstructional })
        assertEquals(setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY), holidayOccurrences.map { it.dayOfWeek }.toSet())
    }

    @Test
    fun `derives baseName and kind from the subject-name suffix`() {
        val timetable = parseOrFail(SINGLE_WEEK_SAMPLE)

        val lecture = timetable.subjects.single { it.id == "SUBJ_LEC" }
        assertEquals("Algorithms and Programming I", lecture.baseName)
        assertEquals(SubjectKind.LECTURE, lecture.kind)

        val practice = timetable.subjects.single { it.id == "SUBJ_PRAC" }
        assertEquals("Algorithms and Programming I", practice.baseName)
        assertEquals(SubjectKind.PRACTICE, practice.kind)

        // No parenthesized suffix -> falls back to the full name, kind OTHER.
        val holiday = timetable.subjects.single { it.id == "SUBJ_HOLIDAY" }
        assertEquals("HOLIDAY", holiday.baseName)
        assertEquals(SubjectKind.OTHER, holiday.kind)
    }

    @Test
    fun `excludes teacherless placeholder lessons from the followable subject set`() {
        val timetable = parseOrFail(SINGLE_WEEK_SAMPLE)

        assertEquals(setOf("SUBJ_LEC"), timetable.followableSubjectIds)
    }

    @Test
    fun `builds cohort to subject mapping from lesson classids`() {
        val timetable = parseOrFail(SINGLE_WEEK_SAMPLE)

        assertEquals(setOf("SUBJ_LEC", "SUBJ_HOLIDAY"), timetable.cohortSubjectIds["CL_IT2"])
        assertEquals(setOf("SUBJ_HOLIDAY"), timetable.cohortSubjectIds["CL_IT1"])
    }

    @Test
    fun `two distinct weeksdefid values on lessons signal multi-week coverage`() {
        val timetable = parseOrFail(MULTI_WEEK_SAMPLE)

        assertEquals(TimetableCoverage.MULTI_WEEK, timetable.coverage)
    }

    @Test
    fun `card without an assigned room only inherits a single unambiguous lesson room`() {
        val timetable = parseOrFail(ROOM_FALLBACK_SAMPLE)

        // Card assigns R2 explicitly -> that wins over the lesson's candidate list.
        val explicit = timetable.occurrences.single { it.subjectId == "SUBJ_EXPLICIT" }
        assertEquals(listOf("R2"), explicit.roomIds)

        // Card has no room and the lesson names one candidate -> inherit it.
        val single = timetable.occurrences.single { it.subjectId == "SUBJ_SINGLE" }
        assertEquals(listOf("R1"), single.roomIds)

        // Card has no room and the lesson lists several candidates -> stay empty rather than
        // render every possible room on the block.
        val ambiguous = timetable.occurrences.single { it.subjectId == "SUBJ_AMBIGUOUS" }
        assertEquals(emptyList(), ambiguous.roomIds)
    }

    @Test
    fun `plain text with no markup is reported as unsupported`() {
        val result = AscTimetableXmlParser.parse("just some plain text, not xml at all".encodeToByteArray())
        assertEquals(Result.Error(TimetableError.UnsupportedFormat), result)
    }

    @Test
    fun `well-formed catalog with no scheduled cards is reported as empty`() {
        val result = AscTimetableXmlParser.parse(NO_CARDS_SAMPLE.encodeToByteArray())
        assertEquals(Result.Error(TimetableError.EmptyTimetable), result)
    }

    @Test
    fun `invalid byte sequence is reported as malformed`() {
        val invalidUtf8 = byteArrayOf(0xFF.toByte(), 0xFE.toByte(), 0x00, 0x01)
        val result = AscTimetableXmlParser.parse(invalidUtf8)
        assertEquals(Result.Error(TimetableError.MalformedXml), result)
    }

    private fun parseOrFail(xml: String): Timetable =
        when (val result = parseAscTimetable(xml.encodeToByteArray(), "sample.xml")) {
            is Result.Success -> result.data
            is Result.Error -> fail("expected a successful parse but got ${result.error}")
        }
}

private const val SINGLE_WEEK_SAMPLE = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<timetable importtype="database" displayname="aSc Timetables 2012 XML">
  <periods options="canadd" columns="period,name,short,starttime,endtime">
    <period name="1." short="1" period="1" starttime="9:00" endtime="10:20"/>
    <period name="2." short="2" period="2" starttime="10:30" endtime="11:50"/>
  </periods>
  <subjects options="canadd" columns="id,name,short,partner_id">
    <subject id="SUBJ_LEC" name="Algorithms and Programming I (lec)" short="AP1 (lec)" partner_id=""/>
    <subject id="SUBJ_PRAC" name="Algorithms and Programming I (prac)" short="AP1 (prac)" partner_id=""/>
    <subject id="SUBJ_HOLIDAY" name="HOLIDAY" short="HOLIDAY" partner_id=""/>
  </subjects>
  <teachers options="canadd" columns="id,name,short">
    <teacher id="T1" name="ABDULLAYEV FARHOD" short="F.ABDULLAEV"/>
  </teachers>
  <classrooms options="canadd" columns="id,name,short">
    <classroom id="R1" name="C205" short="C205"/>
  </classrooms>
  <classes options="canadd" columns="id,name,short">
    <class id="CL_IT2" name="IT2-24" short="IT2-24"/>
    <class id="CL_IT1" name="IT1-24" short="IT1-24"/>
  </classes>
  <lessons options="canadd" columns="id,subjectid,classids,groupids,teacherids,classroomids,periodspercard,periodsperweek,daysdefid,weeksdefid,termsdefid,seminargroup,capacity,partner_id">
    <lesson id="L_LEC" classids="CL_IT2" subjectid="SUBJ_LEC" periodspercard="1" periodsperweek="2.0" teacherids="T1" classroomids="R1" groupids="" seminargroup="" termsdefid="TERM1" weeksdefid="W1" daysdefid="D_ANY" capacity="*" partner_id=""/>
    <lesson id="L_HOLIDAY" classids="CL_IT2,CL_IT1" subjectid="SUBJ_HOLIDAY" periodspercard="2" periodsperweek="4.0" teacherids="" classroomids="" groupids="" seminargroup="" termsdefid="TERM1" weeksdefid="W1" daysdefid="D_ANY" capacity="*" partner_id=""/>
  </lessons>
  <cards options="canadd" columns="lessonid,period,days,weeks,terms,classroomids">
    <card lessonid="L_LEC" classroomids="R1" period="1" weeks="1" terms="1" days="100000"/>
    <card lessonid="L_HOLIDAY" classroomids="" period="1" weeks="1" terms="1" days="111111"/>
    <card lessonid="L_HOLIDAY" classroomids="" period="2" weeks="1" terms="1" days="111111"/>
  </cards>
</timetable>"""

private const val MULTI_WEEK_SAMPLE = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<timetable importtype="database" displayname="aSc Timetables 2012 XML">
  <periods options="canadd" columns="period,name,short,starttime,endtime">
    <period name="1." short="1" period="1" starttime="9:00" endtime="10:20"/>
  </periods>
  <subjects options="canadd" columns="id,name,short,partner_id">
    <subject id="SUBJ_A" name="Subject A (lec)" short="A" partner_id=""/>
  </subjects>
  <teachers options="canadd" columns="id,name,short">
    <teacher id="T1" name="Teacher One" short="T.One"/>
  </teachers>
  <lessons options="canadd" columns="id,subjectid,classids,teacherids,classroomids,weeksdefid">
    <lesson id="L1" classids="C1" subjectid="SUBJ_A" teacherids="T1" classroomids="" weeksdefid="W1" partner_id=""/>
    <lesson id="L2" classids="C1" subjectid="SUBJ_A" teacherids="T1" classroomids="" weeksdefid="W2" partner_id=""/>
  </lessons>
  <cards options="canadd" columns="lessonid,period,days,weeks,terms,classroomids">
    <card lessonid="L1" classroomids="" period="1" weeks="1" terms="1" days="100000"/>
    <card lessonid="L2" classroomids="" period="1" weeks="2" terms="1" days="100000"/>
  </cards>
</timetable>"""

private const val ROOM_FALLBACK_SAMPLE = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<timetable importtype="database" displayname="aSc Timetables 2012 XML">
  <periods options="canadd" columns="period,name,short,starttime,endtime">
    <period name="1." short="1" period="1" starttime="9:00" endtime="10:20"/>
  </periods>
  <subjects options="canadd" columns="id,name,short,partner_id">
    <subject id="SUBJ_EXPLICIT" name="Explicit (lec)" short="EXP" partner_id=""/>
    <subject id="SUBJ_SINGLE" name="Single (lec)" short="SNG" partner_id=""/>
    <subject id="SUBJ_AMBIGUOUS" name="Ambiguous (lec)" short="AMB" partner_id=""/>
  </subjects>
  <teachers options="canadd" columns="id,name,short">
    <teacher id="T1" name="Teacher One" short="T.One"/>
  </teachers>
  <classrooms options="canadd" columns="id,name,short">
    <classroom id="R1" name="101" short="101"/>
    <classroom id="R2" name="102" short="102"/>
    <classroom id="R3" name="103" short="103"/>
  </classrooms>
  <lessons options="canadd" columns="id,subjectid,classids,teacherids,classroomids,weeksdefid">
    <lesson id="L_EXPLICIT" classids="C1" subjectid="SUBJ_EXPLICIT" teacherids="T1" classroomids="R1,R2,R3" weeksdefid="W1" partner_id=""/>
    <lesson id="L_SINGLE" classids="C1" subjectid="SUBJ_SINGLE" teacherids="T1" classroomids="R1" weeksdefid="W1" partner_id=""/>
    <lesson id="L_AMBIGUOUS" classids="C1" subjectid="SUBJ_AMBIGUOUS" teacherids="T1" classroomids="R1,R2,R3" weeksdefid="W1" partner_id=""/>
  </lessons>
  <cards options="canadd" columns="lessonid,period,days,weeks,terms,classroomids">
    <card lessonid="L_EXPLICIT" classroomids="R2" period="1" weeks="1" terms="1" days="100000"/>
    <card lessonid="L_SINGLE" classroomids="" period="1" weeks="1" terms="1" days="100000"/>
    <card lessonid="L_AMBIGUOUS" classroomids="" period="1" weeks="1" terms="1" days="100000"/>
  </cards>
</timetable>"""

private const val NO_CARDS_SAMPLE = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<timetable importtype="database" displayname="aSc Timetables 2012 XML">
  <periods options="canadd" columns="period,name,short,starttime,endtime">
    <period name="1." short="1" period="1" starttime="9:00" endtime="10:20"/>
  </periods>
  <subjects options="canadd" columns="id,name,short,partner_id">
    <subject id="SUBJ_A" name="Subject A (lec)" short="A" partner_id=""/>
  </subjects>
  <cards options="canadd" columns="lessonid,period,days,weeks,terms,classroomids"/>
</timetable>"""
