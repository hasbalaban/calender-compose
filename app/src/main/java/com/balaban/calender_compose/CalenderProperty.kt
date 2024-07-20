package com.balaban.calender_compose

import java.time.LocalDate

class CalenderProperty private constructor(
    val countOldYears: Int = 0,
    val countOldMounts: Int = 0,
    val countNextYears: Int = 0,
    val countNextMounts: Int = 0,
    val calenderDirections: CalenderDirections = CalenderDirections.Vertical,
    val calenderSelectionType: CalenderSelectionType = CalenderSelectionType.Single,
) {
    class Builder {

        private var countOldYears: Int = 0
        private var countOldMounts: Int = 0
        private var countNextYears: Int = 0
        private var countNextMounts: Int = 0
        private var calenderDirections: CalenderDirections = CalenderDirections.Vertical
        private var calenderSelectionType: CalenderSelectionType = CalenderSelectionType.Single

        // Methods for setting properties
        fun countOldYear(countOldYears: Int) = apply { this.countOldYears = countOldYears }
        fun countOldMount(countOldMounts: Int) = apply { this.countOldMounts = countOldMounts }
        fun countNextYear(countNextYears: Int) = apply { this.countNextYears = countNextYears }
        fun countNextMount(countNextMounts: Int) = apply { this.countNextMounts = countNextMounts }
        fun calenderDirection(calenderDirection: CalenderDirections) = apply { this.calenderDirections = calenderDirection }
        fun calenderSelectionType(calenderSelectionType: CalenderSelectionType) = apply { this.calenderSelectionType = calenderSelectionType }

        fun build(): CalenderProperty {
            return CalenderProperty(
                countOldYears,
                countOldMounts,
                countNextYears,
                countNextMounts,
                calenderDirections,
                calenderSelectionType
            )
        }

    }

    enum class CalenderDirections() {
        Horizontal, Vertical
    }


    enum class CalenderSelectionType() {
        Single, Multiple, DateRange
    }

}


operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> = object : Iterator<LocalDate> {
    private var current = start
    override fun hasNext() = current <= endInclusive
    override fun next() = current.apply {
        current = plusDays(1)
    }
}

