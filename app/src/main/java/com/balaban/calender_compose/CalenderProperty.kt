package com.balaban.calender_compose

class CalenderProperty private constructor(
    val countOldYears: Int = 0,
    val countOldMounts: Int = 0,
    val countNextYears: Int = 0,
    val countNextMounts: Int = 0,
    val calenderDirections: CalenderDirections = CalenderDirections.Vertical,
) {
    class Builder {

        private var countOldYears: Int = 0
        private var countOldMounts: Int = 0
        private var countNextYears: Int = 0
        private var countNextMounts: Int = 0
        private var calenderDirections: CalenderDirections = CalenderDirections.Vertical

        // Methods for setting properties
        fun countOldYear(countOldYears: Int) = apply { this.countOldYears = countOldYears }
        fun countOldMount(countOldMounts: Int) = apply { this.countOldMounts = countOldMounts }
        fun countNextYear(countNextYears: Int) = apply { this.countNextYears = countNextYears }
        fun countNextMount(countNextMounts: Int) = apply { this.countNextMounts = countNextMounts }
        fun calenderDirection(calenderDirection: CalenderDirections) = apply { this.calenderDirections = calenderDirection }

        // Build method to create User instance
        fun build(): CalenderProperty {
            return CalenderProperty(countOldYears, countOldMounts, countNextYears, countNextMounts, calenderDirections)
        }

    }

    enum class CalenderDirections() {
        Horizontal, Vertical
    }

}

