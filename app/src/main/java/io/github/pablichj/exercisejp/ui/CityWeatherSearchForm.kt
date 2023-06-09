package io.github.pablichj.exercisejp.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CityWeatherSearchForm(
    modifier: Modifier = Modifier,
    searchFormState: SearchFormState,
    onSubmitClick: (CityWeatherRequest) -> Unit
) {
    var cityName: String by remember(searchFormState) { mutableStateOf(searchFormState.citySearched) }
    var stateCode: String by remember(searchFormState) { mutableStateOf(searchFormState.stateSearched) }
    var selectedIndex by remember(searchFormState) { mutableStateOf(searchFormState.selectedIndex) }
    var expanded by remember { mutableStateOf(false) }
    val units = remember { Units.values().toList() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            value = cityName,
            onValueChange = {
                cityName = it
                searchFormState.citySearched = it
            },
            label = { Text(text = "Enter City Name") }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            value = stateCode,
            onValueChange = {
                stateCode = it
                searchFormState.stateSearched = it
            },
            label = { Text(text = "Enter State Code") }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color.LightGray)
                .clickable {
                    expanded = true
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = units[selectedIndex].name
            )
        }
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .wrapContentHeight(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            properties = PopupProperties(focusable = true),
            offset = DpOffset(0.dp, 0.dp)
        ) {
            units.forEachIndexed { index, unit ->
                DropdownMenuItem(
                    text = {
                        val color = if (index == selectedIndex) {
                            Color.Green
                        } else {
                            Color.Black
                        }
                        Text(text = unit.name, color = color)
                    },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        searchFormState.selectedIndex = index
                        searchFormState.units = units[selectedIndex]
                    }
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                onSubmitClick(
                    CityWeatherRequest(
                        cityName,
                        stateCode,
                        searchFormState.countrySearched,
                        units = units[selectedIndex]
                    )
                )
            }
        ) {
            Text(text = "Submit")
        }
    }
}