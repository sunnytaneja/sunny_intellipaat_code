package com.android.myapplication.domain

import com.android.myapplication.domain.model.Course
import com.android.myapplication.domain.util.ProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun `course with no lessons reports zero progress`() {
        assertEquals(0, ProgressCalculator.percent(completed = 0, total = 0))
    }

    @Test
    fun `progress is rounded to the nearest whole percent`() {
        assertEquals(65, ProgressCalculator.percent(completed = 13, total = 20))
        assertEquals(38, ProgressCalculator.percent(completed = 6, total = 16))
    }

    @Test
    fun `progress never goes above 100`() {
        assertEquals(100, ProgressCalculator.percent(completed = 30, total = 20))
    }

    @Test
    fun `completing a lesson raises the course progress`() {
        val before =
            Course(1, "Python Programming", "John Smith", lessonCount = 4, completedLessons = 1)
        val after = before.copy(completedLessons = before.completedLessons + 1)

        assertEquals(25, before.progress)
        assertEquals(50, after.progress)
    }
}
