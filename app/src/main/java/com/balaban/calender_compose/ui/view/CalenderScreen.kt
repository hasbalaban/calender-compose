package com.balaban.calender_compose.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.balaban.calender_compose.CalenderProperty
import com.balaban.calender_compose.iterator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@Composable
fun CalenderScreen(calenderProperties: CalenderProperty, onDateSelected: (LocalDate) -> Unit) {
    if (calenderProperties.calenderDirections == CalenderProperty.CalenderDirections.Vertical) {
        VerticalCalender(calenderProperties, onDateSelected)
        return
    }
    HorizontalCalender(calenderProperties, onDateSelected)
}

@Composable
private fun VerticalCalender(
    calenderProperties: CalenderProperty,
    onDateSelected: (LocalDate) -> Unit
) {

    var selectedDates: List<LocalDate> by remember {
        mutableStateOf(
            if (calenderProperties.calenderSelectionType == CalenderProperty.CalenderSelectionType.Single) listOf(
                LocalDate.now()
            ) else emptyList()
        )
    }

    val sortedList by remember {
        derivedStateOf {
            selectedDates.sorted()
        }
    }

    val calenderItems =
        remember { CustomCalender().provideMothAccordingInputs(calenderProperties = calenderProperties) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(calenderItems) { detail ->
                val currentMonth = YearMonth.of(detail.year, detail.month)
                val firstDayOfMonth = currentMonth.atDay(1)
                val daysOfWeek = daysOfWeek()
                val daysInMonth = (1..currentMonth.lengthOfMonth()).toList()

                Text(
                    text = "${
                        currentMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        )
                    } ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )


                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayOfWeek in daysOfWeek) {

                        Text(
                            text = dayOfWeek.getDisplayName(
                                TextStyle.SHORT,
                                Locale.getDefault()
                            ),
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column {
                    daysInMonth.chunked(7).forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            week.forEachIndexed { index, day ->
                                val date = firstDayOfMonth.plusDays(day.toLong() - 1)
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (calenderProperties.calenderSelectionType != CalenderProperty.CalenderSelectionType.DateRange && sortedList.contains(
                                                    date
                                                )
                                            ) {
                                                MaterialTheme.colorScheme.primary
                                            } else if (sortedList.isNotEmpty() && date in sortedList.first()..sortedList.last()) {
                                                MaterialTheme.colorScheme.primary
                                            } else Color.Transparent,
                                            when (date) {
                                                sortedList.firstOrNull() -> RoundedCornerShape(50, 0, 0, 50)
                                                sortedList.lastOrNull() -> RoundedCornerShape(0, 50, 50, 0)
                                                else -> RectangleShape
                                            }

                                        )
                                        .clickable {
                                            when (calenderProperties.calenderSelectionType) {
                                                CalenderProperty.CalenderSelectionType.Single -> {
                                                    selectedDates = emptyList()
                                                    selectedDates = selectedDates + date
                                                }

                                                CalenderProperty.CalenderSelectionType.Multiple -> {
                                                    selectedDates =
                                                        if (selectedDates.contains(date)) {
                                                            selectedDates - date
                                                        } else {
                                                            selectedDates + date
                                                        }
                                                }

                                                CalenderProperty.CalenderSelectionType.DateRange -> {
                                                    selectedDates = calculateNewRange(
                                                        selectedDates = sortedList,
                                                        clickedDate = date
                                                    )
                                                }
                                            }

                                        }
                                        .then(
                                            if (week.size == 7) {
                                                Modifier.fillMaxWidth(1f / (7 - index))
                                            } else {
                                                Modifier.fillMaxWidth(1f / (7 - index))
                                            }
                                        )

                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = day.toString(),
                                        color =
                                        if (calenderProperties.calenderSelectionType != CalenderProperty.CalenderSelectionType.DateRange && sortedList.contains(date)) {
                                            Color.White
                                        } else if (sortedList.isNotEmpty() && date in sortedList.first()..sortedList.last()) {
                                            Color.White
                                        } else Color.Unspecified)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 2.dp)
                )

            }


        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HorizontalCalender(
    calenderProperties: CalenderProperty,
    onDateSelected: (LocalDate) -> Unit
) {

    var selectedDates: List<LocalDate> by remember {
        mutableStateOf(
            if (calenderProperties.calenderSelectionType == CalenderProperty.CalenderSelectionType.Single) listOf(
                LocalDate.now()
            ) else emptyList()
        )
    }
    val calenderItems =
        remember { CustomCalender().provideMothAccordingInputs(calenderProperties = calenderProperties) }

    val lazyListState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = lazyListState,
            flingBehavior = flingBehavior
        ) {
            items(calenderItems) { detail ->
                val currentMonth = YearMonth.of(detail.year, detail.month)
                val firstDayOfMonth = currentMonth.atDay(1)
                val daysOfWeek = daysOfWeek()
                val daysInMonth = (1..currentMonth.lengthOfMonth()).toList()

                val sortedList by remember {
                    derivedStateOf {
                        selectedDates.sorted()
                    }
                }


                Column(modifier = Modifier.width(screenWidthPx.dp)) {

                    Text(
                        text = "${
                            currentMonth.month.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            )
                        } ${currentMonth.year}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 4.dp
                        )
                    )
                    HorizontalDivider(
                        color = Color.LightGray.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (dayOfWeek in daysOfWeek) {

                            Text(
                                text = dayOfWeek.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.getDefault()
                                ),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Column (modifier = Modifier.padding(horizontal = 2.dp)) {
                        daysInMonth.chunked(7).forEach { week ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                week.forEachIndexed { index, day ->
                                    val date = firstDayOfMonth.plusDays(day.toLong() - 1)
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 2.dp)
                                            .background(
                                                if (calenderProperties.calenderSelectionType != CalenderProperty.CalenderSelectionType.DateRange && sortedList.contains(
                                                        date
                                                    )
                                                ) {
                                                    MaterialTheme.colorScheme.primary
                                                } else if (sortedList.isNotEmpty() && date in sortedList.first()..sortedList.last()) {
                                                    MaterialTheme.colorScheme.primary
                                                } else Color.Transparent,
                                                when (date) {
                                                    sortedList.firstOrNull() -> RoundedCornerShape(50, 0, 0, 50)
                                                    sortedList.lastOrNull() -> RoundedCornerShape(0, 50, 50, 0)
                                                    else -> RectangleShape
                                                }
                                            )
                                            .clickable {
                                                when (calenderProperties.calenderSelectionType) {
                                                    CalenderProperty.CalenderSelectionType.Single -> {
                                                        selectedDates = emptyList()
                                                        selectedDates = selectedDates + date
                                                    }

                                                    CalenderProperty.CalenderSelectionType.Multiple -> {
                                                        selectedDates =
                                                            if (selectedDates.contains(date)) {
                                                                selectedDates - date
                                                            } else {
                                                                selectedDates + date
                                                            }
                                                    }

                                                    CalenderProperty.CalenderSelectionType.DateRange -> {
                                                        selectedDates = calculateNewRange(
                                                            selectedDates = sortedList,
                                                            clickedDate = date
                                                        )
                                                    }
                                                }

                                            }
                                            .then(
                                                if (week.size == 7) {
                                                    Modifier.fillMaxWidth(1f / (7 - index))
                                                } else {
                                                    Modifier.fillMaxWidth(1f / (7 - index))
                                                }
                                            )

                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = day.toString(),

                                            color =
                                            if (calenderProperties.calenderSelectionType != CalenderProperty.CalenderSelectionType.DateRange && sortedList.contains(
                                                    date
                                                )
                                            ) {
                                                Color.White
                                            } else if (sortedList.isNotEmpty() && date in sortedList.first()..sortedList.last()) {
                                                Color.White
                                            } else Color.Unspecified
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }


            }


        }
        Button(
            onClick = {
                println(selectedDates)
                println(selectedDates)
                println(selectedDates)
                println(selectedDates)
                println(selectedDates)
            },
            modifier = Modifier
                .alpha(0.9f)
                .padding(16.dp)
        ) {
            Text(text = "Show Detail")
        }
    }
}


interface CalenderOperations {
    fun provideMothAccordingInputs(calenderProperties: CalenderProperty): List<YearMonth>
}

class CustomCalender() : CalenderOperations {
    override fun provideMothAccordingInputs(calenderProperties: CalenderProperty): List<YearMonth> {
        val oldCountMounts =
            calenderProperties.countOldMounts + (calenderProperties.countOldYears * 12)
        val nextCountMounts =
            calenderProperties.countNextMounts + (calenderProperties.countNextYears * 12)


        val detailListYearMonth = mutableListOf<YearMonth>()
        var month = YearMonth.now().minusMonths(oldCountMounts.toLong())

        // add Old Month
        for (i in 0..<oldCountMounts) {
            detailListYearMonth.add(month)
            month = month.plusMonths(1)
        }

        // add current Month
        month = YearMonth.now()
        detailListYearMonth.add(month)


        // add next months
        for (i in 1..nextCountMounts) {
            month = month.plusMonths(1)
            detailListYearMonth.add(month)
        }

        return detailListYearMonth
    }

}


private fun calculateNewRange(
    selectedDates: List<LocalDate>,
    clickedDate: LocalDate
): List<LocalDate> {
    val rangeDate = mutableListOf<LocalDate>()
    rangeDate.addAll(selectedDates)

    if (rangeDate.size < 2 && rangeDate.contains(clickedDate)) {
        rangeDate.remove(clickedDate)
        return rangeDate
    }

    if (rangeDate.size < 2 && !rangeDate.contains(clickedDate)) {
        rangeDate.add(clickedDate)
        return rangeDate
    }

    if (rangeDate.contains(clickedDate)) {
        rangeDate.clear()
        rangeDate.add(clickedDate)
        return rangeDate
    }

    if (clickedDate > rangeDate.last()) {
        val newRange = listOf(rangeDate.first(), clickedDate)

        rangeDate.clear()
        rangeDate.addAll(newRange)
        return rangeDate
    }

    if (clickedDate < rangeDate.first()) {
        val newRange = listOf(clickedDate, rangeDate.last())

        rangeDate.clear()
        rangeDate.addAll(newRange)
        return rangeDate
    }


    val range = rangeDate.first()..rangeDate.last()
    val rangeList = mutableListOf<LocalDate>()
    for (item in range) {
        rangeList.add(item)
    }

    val currentItemIndex = rangeList.indexOf(clickedDate)
    val farFromLastItem = rangeList.size - currentItemIndex

    if (farFromLastItem < currentItemIndex) {
        val newRange = listOf(rangeDate.first(), clickedDate)
        rangeDate.clear()
        rangeDate.addAll(newRange)
        return rangeDate
    }

    val newRange = listOf(clickedDate, rangeDate.last())
    rangeDate.clear()
    rangeDate.addAll(newRange)
    return rangeDate
}

fun daysOfWeek(): List<DayOfWeek> {
    val daysOfWeek = DayOfWeek.entries
    val firstDayOfWeek = DayOfWeek.of(Calendar.getInstance().firstDayOfWeek)
    return if (firstDayOfWeek == DayOfWeek.MONDAY) {
        daysOfWeek
    } else {
        val beforeFirstDay = daysOfWeek.takeWhile { it != firstDayOfWeek }
        val fromFirstDay = daysOfWeek.dropWhile { it != firstDayOfWeek }
        fromFirstDay + beforeFirstDay
    }
}