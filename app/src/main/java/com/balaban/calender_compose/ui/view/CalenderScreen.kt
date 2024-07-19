package com.balaban.calender_compose.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.balaban.calender_compose.CalenderProperty
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@Composable
fun CalenderScreen(calenderProperties: CalenderProperty, onDateSelected: (LocalDate) -> Unit) {
    VerticalCalender(calenderProperties, onDateSelected)
}

@Composable
private fun VerticalCalender(
    calenderProperties: CalenderProperty,
    onDateSelected: (LocalDate) -> Unit
) {

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
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
                                            if (date == selectedDate) MaterialTheme.colorScheme.primary else Color.Transparent
                                        )
                                        .clickable { selectedDate = date }
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
                                    Text(text = day.toString())
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
        Button(
            onClick = {
                selectedDate?.let {
                    onDateSelected(selectedDate)
                }
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