package com.balaban.calender_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.balaban.calender_compose.ui.theme.CalendercomposeTheme
import com.balaban.calender_compose.ui.view.CalenderScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendercomposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        onDateSelected = {

                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen( onDateSelected : () -> Unit, modifier: Modifier = Modifier) {
    val calenderProperties : CalenderProperty = CalenderProperty.Builder()
        .countOldYear(1)
        .countOldMount(2)
        .countNextYear(1)
        .countNextMount(2)
        .calenderDirection(CalenderProperty.CalenderDirections.Horizontal)
        .build()

    CalenderScreen(calenderProperties = calenderProperties) {
        onDateSelected()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalendercomposeTheme {
        MainScreen(onDateSelected = {

        })
    }
}